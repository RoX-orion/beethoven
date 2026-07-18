package org.beethoven.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.beethoven.lib.AuthContext;
import org.beethoven.lib.exception.AuthenticationException;
import org.beethoven.lib.exception.BeethovenException;
import org.beethoven.mapper.MusicMapper;
import org.beethoven.mapper.PlayQueueItemMapper;
import org.beethoven.mapper.PlayQueueMapper;
import org.beethoven.mapper.PlaylistMapper;
import org.beethoven.pojo.dto.*;
import org.beethoven.pojo.entity.Music;
import org.beethoven.pojo.entity.PlayQueue;
import org.beethoven.pojo.entity.PlayQueueItem;
import org.beethoven.pojo.entity.Playlist;
import org.beethoven.pojo.enums.PlayMode;
import org.beethoven.pojo.vo.MusicInfo;
import org.beethoven.pojo.vo.PlayQueueItemVo;
import org.beethoven.pojo.vo.PlayQueueVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class QueueService {

    private static final PlayMode DEFAULT_PLAY_MODE = PlayMode.LOOP;

    @Resource
    private PlaylistMapper playlistMapper;

    @Resource
    private MusicMapper musicMapper;

    @Resource
    private PlayQueueMapper playQueueMapper;

    @Resource
    private PlayQueueItemMapper playQueueItemMapper;

    @Resource
    private AuthContext authContext;

    public PlayQueueVo getQueue() {
        String userId = requireUserId();
        PlayQueue queue = getUserQueue(userId);
        if (queue == null) {
            return emptyQueue(userId);
        }
        return toVo(queue);
    }

    @Transactional
    public PlayQueueVo buildFromPlaylist(BuildQueueFromPlaylistDTO buildQueueFromPlaylistDTO) {
        String userId = requireUserId();
        Playlist playlist = playlistMapper.selectById(buildQueueFromPlaylistDTO.getPlaylistId());
        if (playlist == null) {
            throw new BeethovenException("歌单不存在");
        }
        if (!Boolean.TRUE.equals(playlist.getAccessible()) && !Objects.equals(playlist.getCreator(), userId)) {
            throw new BeethovenException("没有权限播放此歌单");
        }

        List<MusicInfo> musicList = playlistMapper.getPlaylistMusic(buildQueueFromPlaylistDTO.getPlaylistId(), null);
        List<String> musicIds = musicList.stream()
                .map(MusicInfo::getId)
                .toList();

        return resetQueue(
                userId,
                musicIds,
                buildQueueFromPlaylistDTO.getStartMusicId(),
                resolvePlayMode(buildQueueFromPlaylistDTO.getPlayMode()),
                "playlist",
                buildQueueFromPlaylistDTO.getPlaylistId()
        );
    }

    @Transactional
    public PlayQueueVo buildFromMusicList(BuildQueueFromMusicListDTO buildQueueFromMusicListDTO) {
        String userId = requireUserId();
        ensureMusicExists(buildQueueFromMusicListDTO.getMusicIds());
        return resetQueue(
                userId,
                buildQueueFromMusicListDTO.getMusicIds(),
                buildQueueFromMusicListDTO.getStartMusicId(),
                resolvePlayMode(buildQueueFromMusicListDTO.getPlayMode()),
                buildQueueFromMusicListDTO.getSourceType(),
                buildQueueFromMusicListDTO.getSourceId()
        );
    }

    @Transactional
    public PlayQueueVo addToEnd(AddQueueItemDTO addQueueItemDTO) {
        String userId = requireUserId();
        ensureMusicExists(addQueueItemDTO.getMusicId());
        PlayQueue queue = getOrCreateQueue(userId);
        Integer maxSortNo = playQueueItemMapper.getMaxSortNo(queue.getId());
        int sortNo = maxSortNo == null ? 0 : maxSortNo + 1;
        insertQueueItem(queue.getId(), addQueueItemDTO.getMusicId(), sortNo, addQueueItemDTO.getSourceType(), addQueueItemDTO.getSourceId());
        if (queue.getCurrentIndex() == null || queue.getCurrentIndex() < 0) {
            queue.setCurrentIndex(0);
            queue.setCurrentMusicId(addQueueItemDTO.getMusicId());
        }
        touchQueue(queue);
        return toVo(queue);
    }

    @Transactional
    public PlayQueueVo addToNext(AddQueueItemDTO addQueueItemDTO) {
        String userId = requireUserId();
        ensureMusicExists(addQueueItemDTO.getMusicId());
        PlayQueue queue = getOrCreateQueue(userId);
        int insertSortNo = Math.max(queue.getCurrentIndex() == null ? 0 : queue.getCurrentIndex() + 1, 0);
        playQueueItemMapper.shiftSortNoFrom(queue.getId(), insertSortNo);
        insertQueueItem(queue.getId(), addQueueItemDTO.getMusicId(), insertSortNo, addQueueItemDTO.getSourceType(), addQueueItemDTO.getSourceId());
        if (queue.getCurrentIndex() == null || queue.getCurrentIndex() < 0) {
            queue.setCurrentIndex(0);
            queue.setCurrentMusicId(addQueueItemDTO.getMusicId());
        }
        touchQueue(queue);
        return toVo(queue);
    }

    @Transactional
    public PlayQueueVo reorder(ReorderQueueDTO reorderQueueDTO) {
        String userId = requireUserId();
        PlayQueue queue = requireQueue(userId);
        List<PlayQueueItemVo> currentItems = playQueueItemMapper.getQueueItems(queue.getId());
        if (currentItems.size() != reorderQueueDTO.getQueueItemIds().size()) {
            throw new BeethovenException("播放队列项不匹配");
        }

        Set<String> currentIds = new HashSet<>();
        for (PlayQueueItemVo item : currentItems) {
            currentIds.add(item.getQueueItemId());
        }
        Set<String> requestedIds = new HashSet<>(reorderQueueDTO.getQueueItemIds());
        if (!currentIds.equals(requestedIds)) {
            throw new BeethovenException("播放队列项不匹配");
        }

        for (int i = 0; i < reorderQueueDTO.getQueueItemIds().size(); i++) {
            PlayQueueItem item = new PlayQueueItem();
            item.setId(reorderQueueDTO.getQueueItemIds().get(i));
            item.setSortNo(i);
            playQueueItemMapper.updateById(item);
        }

        syncCurrentIndex(queue);
        touchQueue(queue);
        return toVo(queue);
    }

    @Transactional
    public PlayQueueVo updateCurrent(UpdateCurrentQueueDTO updateCurrentQueueDTO) {
        String userId = requireUserId();
        PlayQueue queue = requireQueue(userId);
        List<PlayQueueItemVo> items = playQueueItemMapper.getQueueItems(queue.getId());
        if (items.isEmpty()) {
            queue.setCurrentIndex(-1);
            queue.setCurrentMusicId(null);
        } else if (updateCurrentQueueDTO.getCurrentIndex() != null) {
            if (updateCurrentQueueDTO.getCurrentIndex() < 0 || updateCurrentQueueDTO.getCurrentIndex() >= items.size()) {
                throw new BeethovenException("当前播放索引不合法");
            }
            queue.setCurrentIndex(updateCurrentQueueDTO.getCurrentIndex());
            queue.setCurrentMusicId(items.get(updateCurrentQueueDTO.getCurrentIndex()).getMusicId());
        } else if (StringUtils.hasText(updateCurrentQueueDTO.getMusicId())) {
            int index = indexOfMusic(items, updateCurrentQueueDTO.getMusicId());
            if (index < 0) {
                throw new BeethovenException("当前歌曲不在播放队列中");
            }
            queue.setCurrentIndex(index);
            queue.setCurrentMusicId(updateCurrentQueueDTO.getMusicId());
        }
        if (updateCurrentQueueDTO.getCurrentTime() != null) {
            queue.setCurrentTime(Math.max(updateCurrentQueueDTO.getCurrentTime(), 0));
        }
        touchQueue(queue);
        return toVo(queue);
    }

    @Transactional
    public PlayQueueVo updatePlayMode(PlayMode playMode) {
        String userId = requireUserId();
        PlayQueue queue = getOrCreateQueue(userId);
        queue.setPlayMode(resolvePlayMode(playMode));
        touchQueue(queue);
        return toVo(queue);
    }

    @Transactional
    public PlayQueueVo removeItem(String queueItemId) {
        String userId = requireUserId();
        PlayQueue queue = requireQueue(userId);
        PlayQueueItem item = playQueueItemMapper.selectOne(
                new LambdaQueryWrapper<PlayQueueItem>()
                        .eq(PlayQueueItem::getId, queueItemId)
                        .eq(PlayQueueItem::getQueueId, queue.getId())
        );
        if (item == null) {
            throw new BeethovenException("播放队列项不存在");
        }

        int removedSortNo = item.getSortNo();
        int previousIndex = queue.getCurrentIndex() == null ? -1 : queue.getCurrentIndex();
        playQueueItemMapper.deleteById(queueItemId);
        playQueueItemMapper.decrementSortNoAfter(queue.getId(), removedSortNo);

        List<PlayQueueItemVo> items = playQueueItemMapper.getQueueItems(queue.getId());
        if (items.isEmpty()) {
            queue.setCurrentIndex(-1);
            queue.setCurrentMusicId(null);
        } else if (removedSortNo < previousIndex) {
            queue.setCurrentIndex(previousIndex - 1);
            queue.setCurrentMusicId(items.get(queue.getCurrentIndex()).getMusicId());
        } else if (removedSortNo == previousIndex) {
            int nextIndex = Math.min(previousIndex, items.size() - 1);
            queue.setCurrentIndex(nextIndex);
            queue.setCurrentMusicId(items.get(nextIndex).getMusicId());
        }

        touchQueue(queue);
        return toVo(queue);
    }

    @Transactional
    public PlayQueueVo clear() {
        String userId = requireUserId();
        PlayQueue queue = getUserQueue(userId);
        if (queue == null) {
            return emptyQueue(userId);
        }
        playQueueItemMapper.delete(new LambdaQueryWrapper<PlayQueueItem>().eq(PlayQueueItem::getQueueId, queue.getId()));
        queue.setSourceType(null);
        queue.setSourceId(null);
        queue.setCurrentMusicId(null);
        queue.setCurrentIndex(-1);
        queue.setCurrentTime(0);
        touchQueue(queue);
        return toVo(queue);
    }

    private PlayQueueVo resetQueue(String userId,
                                   List<String> musicIds,
                                   String startMusicId,
                                   PlayMode playMode,
                                   String sourceType,
                                   String sourceId) {
        PlayQueue queue = getOrCreateQueue(userId);
        playQueueItemMapper.delete(new LambdaQueryWrapper<PlayQueueItem>().eq(PlayQueueItem::getQueueId, queue.getId()));

        int currentIndex = -1;
        String currentMusicId = null;
        for (int i = 0; i < musicIds.size(); i++) {
            String musicId = musicIds.get(i);
            insertQueueItem(queue.getId(), musicId, i, sourceType, sourceId);
            if ((StringUtils.hasText(startMusicId) && Objects.equals(startMusicId, musicId))
                    || (!StringUtils.hasText(startMusicId) && i == 0)) {
                currentIndex = i;
                currentMusicId = musicId;
            }
        }

        if (!musicIds.isEmpty() && currentIndex < 0) {
            currentIndex = 0;
            currentMusicId = musicIds.getFirst();
        }

        queue.setSourceType(sourceType);
        queue.setSourceId(sourceId);
        queue.setPlayMode(playMode);
        queue.setCurrentIndex(currentIndex);
        queue.setCurrentMusicId(currentMusicId);
        queue.setCurrentTime(0);
        touchQueue(queue);
        return toVo(queue);
    }

    private PlayQueue getOrCreateQueue(String userId) {
        PlayQueue queue = getUserQueue(userId);
        if (queue != null) {
            return queue;
        }

        queue = new PlayQueue();
        queue.setUserId(userId);
        queue.setPlayMode(DEFAULT_PLAY_MODE);
        queue.setCurrentIndex(-1);
        queue.setCurrentTime(0);
        queue.setVersion(0L);
        playQueueMapper.insert(queue);
        return queue;
    }

    private PlayQueue requireQueue(String userId) {
        PlayQueue queue = getUserQueue(userId);
        if (queue == null) {
            throw new BeethovenException("播放队列不存在");
        }
        return queue;
    }

    private PlayQueue getUserQueue(String userId) {
        return playQueueMapper.selectOne(new LambdaQueryWrapper<PlayQueue>().eq(PlayQueue::getUserId, userId));
    }

    private void insertQueueItem(String queueId, String musicId, int sortNo, String sourceType, String sourceId) {
        PlayQueueItem item = new PlayQueueItem();
        item.setQueueId(queueId);
        item.setMusicId(musicId);
        item.setSortNo(sortNo);
        item.setSourceType(sourceType);
        item.setSourceId(sourceId);
        playQueueItemMapper.insert(item);
    }

    private void ensureMusicExists(List<String> musicIds) {
        if (musicIds == null || musicIds.isEmpty()) {
            throw new BeethovenException("播放队列不能为空");
        }
        List<String> missingIds = new ArrayList<>();
        for (String musicId : musicIds) {
            if (!StringUtils.hasText(musicId) || !musicExists(musicId)) {
                missingIds.add(musicId);
            }
        }
        if (!missingIds.isEmpty()) {
            throw new BeethovenException("歌曲不存在: " + missingIds);
        }
    }

    private void ensureMusicExists(String musicId) {
        if (!StringUtils.hasText(musicId) || !musicExists(musicId)) {
            throw new BeethovenException("歌曲不存在");
        }
    }

    private boolean musicExists(String musicId) {
        return musicMapper.exists(new LambdaQueryWrapper<Music>().eq(Music::getId, musicId));
    }

    private void syncCurrentIndex(PlayQueue queue) {
        if (!StringUtils.hasText(queue.getCurrentMusicId())) {
            return;
        }
        List<PlayQueueItemVo> items = playQueueItemMapper.getQueueItems(queue.getId());
        int index = indexOfMusic(items, queue.getCurrentMusicId());
        if (index >= 0) {
            queue.setCurrentIndex(index);
        }
    }

    private int indexOfMusic(List<PlayQueueItemVo> items, String musicId) {
        for (int i = 0; i < items.size(); i++) {
            if (Objects.equals(items.get(i).getMusicId(), musicId)) {
                return i;
            }
        }
        return -1;
    }

    private void touchQueue(PlayQueue queue) {
        queue.setVersion(queue.getVersion() == null ? 1L : queue.getVersion() + 1);
        playQueueMapper.updateById(queue);
    }

    private PlayMode resolvePlayMode(PlayMode playMode) {
        return playMode == null ? DEFAULT_PLAY_MODE : playMode;
    }

    private PlayQueueVo toVo(PlayQueue queue) {
        PlayQueueVo playQueueVo = new PlayQueueVo();
        playQueueVo.setId(queue.getId());
        playQueueVo.setUserId(queue.getUserId());
        playQueueVo.setSourceType(queue.getSourceType());
        playQueueVo.setSourceId(queue.getSourceId());
        playQueueVo.setPlayMode(resolvePlayMode(queue.getPlayMode()));
        playQueueVo.setCurrentMusicId(queue.getCurrentMusicId());
        playQueueVo.setCurrentIndex(queue.getCurrentIndex() == null ? -1 : queue.getCurrentIndex());
        playQueueVo.setCurrentTime(queue.getCurrentTime() == null ? 0 : queue.getCurrentTime());
        playQueueVo.setRandomSeed(queue.getRandomSeed());
        playQueueVo.setVersion(queue.getVersion() == null ? 0L : queue.getVersion());
        playQueueVo.setItems(playQueueItemMapper.getQueueItems(queue.getId()));

        return playQueueVo;
    }

    private PlayQueueVo emptyQueue(String userId) {
        PlayQueueVo playQueueVo = new PlayQueueVo();
        playQueueVo.setUserId(userId);
        playQueueVo.setPlayMode(DEFAULT_PLAY_MODE);
        playQueueVo.setCurrentIndex(-1);
        playQueueVo.setCurrentTime(0);
        playQueueVo.setVersion(0L);
        playQueueVo.setItems(List.of());

        return playQueueVo;
    }

    private String requireUserId() {
        String userId = authContext.getUserId();
        if (!StringUtils.hasText(userId)) {
            throw new AuthenticationException("Get null userId");
        }
        return userId;
    }
}

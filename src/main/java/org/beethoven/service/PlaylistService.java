package org.beethoven.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.beethoven.lib.Constant;
import org.beethoven.lib.store.StorageContext;
import org.beethoven.lib.store.StorageResponse;
import org.beethoven.mapper.MusicMapper;
import org.beethoven.mapper.MusicPlaylistMapper;
import org.beethoven.mapper.PlaylistMapper;
import org.beethoven.pojo.PageParam;
import org.beethoven.pojo.dto.MusicPlaylistDTO;
import org.beethoven.pojo.dto.PlaylistDTO;
import org.beethoven.pojo.entity.ApiResult;
import org.beethoven.pojo.entity.Music;
import org.beethoven.pojo.entity.MusicPlaylist;
import org.beethoven.pojo.entity.Playlist;
import org.beethoven.pojo.vo.MusicVo;
import org.beethoven.pojo.vo.PlaylistVo;
import org.beethoven.util.Helpers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-10-20
 */

@Service
public class PlaylistService {

    @Resource
    private PlaylistMapper playlistMapper;

    @Resource
    private MusicMapper musicMapper;

    @Resource
    private MusicPlaylistMapper musicPlaylistMapper;

    @Resource
    private AuthService authService;

    @Resource
    private StorageContext storageContext;

    public List<PlaylistVo> getPlayList(PlaylistDTO playlistDTO) {
        int offset = (playlistDTO.getPage() - 1) * playlistDTO.getSize();
        return playlistMapper.getPlayList(offset, playlistDTO.getSize());
    }

    public void addPlaylist(@Valid PlaylistDTO playlistInfo) {
        Playlist playlist = new Playlist();
        playlist.setCreator(authService.getUserId());
        playlist.setTitle(playlistInfo.getTitle());
        playlist.setIntroduction(playlistInfo.getIntroduction());
        playlist.setMusicCount(0);
        playlist.setAccessible(playlistInfo.getAccessible());

        playlistMapper.insert(playlist);
    }

    @Transactional
    public ApiResult<String> addMusicToPlaylist(@Valid MusicPlaylistDTO musicPlaylistDTO) {
        if (!musicMapper.exists(new LambdaQueryWrapper<Music>().eq(Music::getId, musicPlaylistDTO.getMusicId()))) {
            return ApiResult.fail("歌曲不存在！");
        }
        for (Long playlistId : musicPlaylistDTO.getPlaylistIds()) {
            Playlist playlist = playlistMapper.selectOne(new LambdaQueryWrapper<Playlist>().eq(Playlist::getId, playlistId));
            if (Objects.isNull(playlist)) {
                return ApiResult.fail("歌单不存在！");
            }

            if (!musicPlaylistMapper.exists(
                    new LambdaQueryWrapper<MusicPlaylist>()
                            .eq(MusicPlaylist::getMusicId, musicPlaylistDTO.getMusicId())
                            .eq(MusicPlaylist::getPlaylistId, playlistId))) {
                MusicPlaylist musicPlaylist = new MusicPlaylist();
                musicPlaylist.setMusicId(musicPlaylistDTO.getMusicId());
                musicPlaylist.setPlaylistId(playlistId);

                musicPlaylistMapper.insert(musicPlaylist);

                playlist.setMusicCount(playlist.getMusicCount() + 1);
                playlistMapper.updateById(playlist);
            }
        }

        return ApiResult.ok();
    }

    public List<MusicVo> getPlaylistMusic(Long playlistId, Integer page, Integer size) {
        PageParam pageParam = Helpers.buildPageParam(page, size);

        return playlistMapper.getPlaylistMusic(playlistId, pageParam);
    }

    public PlaylistVo getPlaylistInfo(Long playlistId) {
        return playlistMapper.getPlaylistInfo(playlistId);
    }

    public ApiResult<Void> updatePlaylist(PlaylistDTO playlistDTO) {
        Playlist record = playlistMapper.selectOne(new LambdaQueryWrapper<Playlist>().eq(Playlist::getId, playlistDTO.getId()));
        if (record == null) {
            return ApiResult.fail("歌单不存在");
        }

        MultipartFile coverFile = playlistDTO.getCoverFile();
        String ossCoverName = Constant.COVER_DIR + Helpers.buildOssFileName(coverFile.getOriginalFilename());
        Playlist playlist = new Playlist();
        try {
            StorageResponse uploadCoverResponse = storageContext.upload(coverFile.getInputStream(), ossCoverName);
            if (uploadCoverResponse.isOk) {
                playlist.setCover(ossCoverName);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        playlist.setId(playlistDTO.getId());
        playlist.setTitle(playlistDTO.getTitle());
        playlist.setAccessible(playlistDTO.getAccessible());
        playlist.setIntroduction(playlistDTO.getIntroduction());
        playlistMapper.updateById(playlist);

        if (StringUtils.hasText(record.getCover())) {
            storageContext.remove(record.getCover());
        }

        return ApiResult.ok();
    }

    public List<PlaylistVo> getHomePlaylist(String key, Integer page, Integer size) {
        PageParam pageParam = Helpers.buildPageParam(page, size);
        key = StringUtils.hasText(key) ? Helpers.buildFuzzySearchParam(key) : null;
        return playlistMapper.getHomePlaylist(key, pageParam);
    }
}

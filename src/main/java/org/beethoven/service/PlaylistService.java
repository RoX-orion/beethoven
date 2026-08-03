package org.beethoven.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.beethoven.lib.AuthContext;
import org.beethoven.lib.Constant;
import org.beethoven.lib.exception.AuthenticationException;
import org.beethoven.lib.exception.BeethovenException;
import org.beethoven.lib.store.StorageContext;
import org.beethoven.lib.store.StorageResponse;
import org.beethoven.mapper.*;
import org.beethoven.pojo.PageParam;
import org.beethoven.pojo.dto.MusicPlaylistDTO;
import org.beethoven.pojo.dto.PlaylistDTO;
import org.beethoven.pojo.entity.*;
import org.beethoven.pojo.vo.MusicInfo;
import org.beethoven.pojo.vo.PlaylistVo;
import org.beethoven.util.FileUtil;
import org.beethoven.util.Helpers;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
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
@Slf4j
public class PlaylistService {

    @Resource
    private PlaylistMapper playlistMapper;

    @Resource
    private MusicMapper musicMapper;

    @Resource
    private MusicPlaylistMapper musicPlaylistMapper;

    @Resource
    private UserPlaylistMapper userPlaylistMapper;

    @Resource
    private FileInfoMapper fileInfoMapper;

    @Resource
    private StorageContext storageContext;

    @Resource
    private AuthContext authContext;

    public List<PlaylistVo> getSelfPlayList(PlaylistDTO playlistDTO) {
        String userId = authContext.getUserId();
        if (userId == null) {
            return Lists.newArrayList();
        }
        int offset = (playlistDTO.getPage() - 1) * playlistDTO.getSize();
        return playlistMapper.getSelfPlayList(offset, playlistDTO.getSize(), userId);
    }

    @Transactional
    public ApiResult<String> addPlaylist(PlaylistDTO playlistInfo) throws IOException {
        String userId = authContext.getUserId();
        if (userId == null)
            throw new AuthenticationException("Get null userId");
        Playlist playlist = new Playlist();
        playlist.setCreator(userId);
        playlist.setTitle(playlistInfo.getTitle());
        playlist.setIntroduction(playlistInfo.getIntroduction());
        playlist.setMusicCount(0);
        playlist.setAccessible(playlistInfo.getAccessible());
        MultipartFile coverFile = playlistInfo.getCoverFile();
        if (coverFile != null) {
            String coverMime = coverFile.getContentType();
            if (!FileUtil.checkMime(coverMime, FileUtil.FileType.IMAGE)) {
                return ApiResult.fail(String.format("cover file content type[%s] not support!", coverMime));
            }
            String ossCoverName = Constant.COVER_DIR + Helpers.buildOssFileName(coverFile.getOriginalFilename());
            try(InputStream coverInputStream = coverFile.getInputStream()) {
                StorageResponse uploadCoverResponse = storageContext.upload(
                        coverInputStream,
                        ossCoverName,
                        coverFile.getSize()
                );
                if (uploadCoverResponse == null || !uploadCoverResponse.isOk()) {
                    throw new BeethovenException("Upload cover file failed!");
                }

                FileInfo coverFileInfo = new FileInfo();
                coverFileInfo.setOriginalFilename(coverFile.getOriginalFilename());
                coverFileInfo.setFilename(ossCoverName);
                coverFileInfo.setSize(coverFile.getSize());
                coverFileInfo.setMime(coverMime);
                coverFileInfo.setChecksum("");
                coverFileInfo.setStorage(storageContext.getProvider());
                coverFileInfo.setHash(uploadCoverResponse.getHash());
                fileInfoMapper.insert(coverFileInfo);
                playlist.setCoverFileId(coverFileInfo.getId());
            }
        }

        playlistMapper.insert(playlist);

        UserPlaylist userPlaylist = new UserPlaylist();
        userPlaylist.setAccountId(userId);
        userPlaylist.setPlaylistId(playlist.getId());
        userPlaylistMapper.insert(userPlaylist);

        return ApiResult.ok();
    }

    @Transactional
    public ApiResult<String> addMusicToPlaylist(@Valid MusicPlaylistDTO musicPlaylistDTO) {
        String userId = authContext.getUserId();
        if (!StringUtils.hasText(userId)) {
            return ApiResult.expired(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        }
        if (!musicMapper.exists(new LambdaQueryWrapper<Music>().eq(Music::getId, musicPlaylistDTO.getMusicId()))) {
            return ApiResult.fail("歌曲不存在！");
        }

        // 先校验所有歌单是否存在以及歌曲是否已存在，避免部分插入
        for (String playlistId : musicPlaylistDTO.getPlaylistIds()) {
            Playlist playlist = playlistMapper.selectOne(new LambdaQueryWrapper<Playlist>().eq(Playlist::getId, playlistId));
            if (Objects.isNull(playlist)) {
                return ApiResult.fail("歌单不存在！");
            }
            if (!Objects.equals(playlist.getCreator(), userId)) {
                return ApiResult.fail(HttpStatus.FORBIDDEN.value(), "不能操作不属于自己的歌单");
            }
            if (musicPlaylistMapper.exists(
                    new LambdaQueryWrapper<MusicPlaylist>()
                            .eq(MusicPlaylist::getMusicId, musicPlaylistDTO.getMusicId())
                            .eq(MusicPlaylist::getPlaylistId, playlistId))) {
                return ApiResult.fail("歌曲在歌单中已存在!");
            }
        }

        // 校验通过后批量插入
        for (String playlistId : musicPlaylistDTO.getPlaylistIds()) {
            MusicPlaylist musicPlaylist = new MusicPlaylist();
            musicPlaylist.setMusicId(musicPlaylistDTO.getMusicId());
            musicPlaylist.setPlaylistId(playlistId);
            musicPlaylistMapper.insert(musicPlaylist);
        }

        return ApiResult.ok();
    }

    public List<MusicInfo> getPlaylistMusic(String playlistId, Integer page, Integer size) {
        requireReadablePlaylist(playlistId);
        PageParam pageParam = Helpers.buildPageParam(page, size);

        return playlistMapper.getPlaylistMusic(playlistId, pageParam);
    }

    public PlaylistVo getPlaylistInfo(String playlistId) {
        requireReadablePlaylist(playlistId);
        return playlistMapper.getPlaylistInfo(playlistId);
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiResult<Void> updatePlaylist(PlaylistDTO playlistDTO) {
        String userId = authContext.getUserId();
        if (!StringUtils.hasText(userId)) {
            return ApiResult.expired(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        }
        Playlist playlist = playlistMapper.selectOne(new LambdaQueryWrapper<Playlist>().eq(Playlist::getId, playlistDTO.getId()));
        if (playlist == null) {
            return ApiResult.fail("歌单不存在");
        }
        if (!Objects.equals(playlist.getCreator(), userId)) {
            return ApiResult.fail(HttpStatus.FORBIDDEN.value(), "不能操作不属于自己的歌单");
        }

        MultipartFile coverFile = playlistDTO.getCoverFile();
        FileInfo oldCoverFileInfo = null;
        if (coverFile != null) {
            String coverMime = coverFile.getContentType();
            if (!FileUtil.checkMime(coverMime, FileUtil.FileType.IMAGE)) {
                return ApiResult.fail(String.format("cover file content type[%s] not support!", coverMime));
            }
            String ossCoverName = Constant.COVER_DIR + Helpers.buildOssFileName(coverFile.getOriginalFilename());
            oldCoverFileInfo = playlist.getCoverFileId() == null ? null : fileInfoMapper.selectById(playlist.getCoverFileId());
            try {
                FileInfo coverFileInfo = new FileInfo();
                coverFileInfo.setOriginalFilename(coverFile.getOriginalFilename());
                coverFileInfo.setFilename(ossCoverName);
                coverFileInfo.setSize(coverFile.getSize());
                coverFileInfo.setMime(coverMime);
                coverFileInfo.setChecksum("");
                coverFileInfo.setStorage(storageContext.getProvider());
                try (InputStream coverInputStream = coverFile.getInputStream()) {
                    StorageResponse uploadCoverResponse = storageContext.upload(
                            coverInputStream,
                            ossCoverName,
                            coverFile.getSize()
                    );
                    if (uploadCoverResponse == null || !uploadCoverResponse.isOk()) {
                        throw new BeethovenException("Upload cover file failed!");
                    }
                    coverFileInfo.setHash(uploadCoverResponse.getHash());
                }
                // Keep the old object until the new object has been uploaded and referenced.
                fileInfoMapper.insert(coverFileInfo);
                playlist.setCoverFileId(coverFileInfo.getId());
            } catch (IOException e) {
                throw new BeethovenException("Upload cover file error: " + e.getMessage());
            }
        }
        playlist.setId(playlistDTO.getId());
        playlist.setTitle(playlistDTO.getTitle());
        playlist.setAccessible(playlistDTO.getAccessible());
        playlist.setIntroduction(playlistDTO.getIntroduction());
        playlistMapper.updateById(playlist);

        if (coverFile != null) {
            removeReplacedFileAfterCommit(oldCoverFileInfo);
        }

        return ApiResult.ok();
    }

    public List<PlaylistVo> getHomePlaylist(String key, Integer page, Integer size) {
        PageParam pageParam = Helpers.buildPageParam(page, size);
        key = StringUtils.hasText(key) ? Helpers.buildFuzzySearchParam(key) : null;
        return playlistMapper.getHomePlaylist(key, pageParam);
    }

    public ApiResult<String> removeMusic(String playlistId, String musicId) {
        String userId = authContext.getUserId();
        if (userId == null) {
            return ApiResult.expired(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        }
        MusicPlaylist musicPlaylist = musicPlaylistMapper.selectOne(
                new LambdaQueryWrapper<MusicPlaylist>()
                        .eq(MusicPlaylist::getPlaylistId, playlistId)
                        .eq(MusicPlaylist::getMusicId, musicId)
        );
        if (musicPlaylist == null) {
            return ApiResult.fail("The record does not exist");
        }
        Playlist playlist = playlistMapper.selectById(musicPlaylist.getPlaylistId());
        if (playlist == null) {
            return ApiResult.fail("The playlist does not exist");
        }
        if (!playlist.getCreator().equals(userId)) {
            return ApiResult.fail("Cannot operate playlists that do not belong to you");
        }
        musicPlaylistMapper.deleteById(musicPlaylist.getId());

        return ApiResult.ok();
    }

    private void requireReadablePlaylist(String playlistId) {
        Playlist playlist = playlistMapper.selectById(playlistId);
        if (playlist == null) {
            throw new BeethovenException("歌单不存在");
        }
        if (Boolean.TRUE.equals(playlist.getAccessible())) {
            return;
        }
        String userId = authContext.getUserId();
        if (!Objects.equals(playlist.getCreator(), userId)) {
            throw new BeethovenException("没有权限访问此歌单");
        }
    }

    private void removeReplacedFileAfterCommit(FileInfo oldFileInfo) {
        if (oldFileInfo == null) {
            return;
        }
        fileInfoMapper.deleteById(oldFileInfo.getId());
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    removeStorageObject(oldFileInfo);
                }
            });
            return;
        }
        removeStorageObject(oldFileInfo);
    }

    private void removeStorageObject(FileInfo oldFileInfo) {
        try {
            storageContext.remove(oldFileInfo.getFilename());
        } catch (RuntimeException e) {
            // The new reference is already persisted; the old object can be retried by a cleanup task.
            log.warn("Failed to remove replaced playlist cover {}", oldFileInfo.getFilename(), e);
        }
    }
}

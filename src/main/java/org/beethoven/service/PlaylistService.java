package org.beethoven.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.beethoven.lib.AuthContext;
import org.beethoven.lib.Constant;
import org.beethoven.lib.exception.AuthenticationException;
import org.beethoven.lib.store.StorageContext;
import org.beethoven.lib.store.StorageResponse;
import org.beethoven.mapper.*;
import org.beethoven.pojo.PageParam;
import org.beethoven.pojo.dto.MusicPlaylistDTO;
import org.beethoven.pojo.dto.PlaylistDTO;
import org.beethoven.pojo.entity.*;
import org.beethoven.pojo.enums.StorageProvider;
import org.beethoven.pojo.vo.MusicInfo;
import org.beethoven.pojo.vo.PlaylistVo;
import org.beethoven.util.FileUtil;
import org.beethoven.util.Helpers;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        Long userId = authContext.getUserId();
        if (userId == null) {
            return Lists.newArrayList();
        }
        int offset = (playlistDTO.getPage() - 1) * playlistDTO.getSize();
        return playlistMapper.getSelfPlayList(offset, playlistDTO.getSize(), userId);
    }

    @Transactional
    public ApiResult<String> addPlaylist(PlaylistDTO playlistInfo) throws IOException {
        Long userId = authContext.getUserId();
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
                FileInfo coverFileInfo = new FileInfo();
                coverFileInfo.setOriginalFilename(coverFile.getOriginalFilename());
                coverFileInfo.setFilename(ossCoverName);
                coverFileInfo.setSize(coverFile.getSize());
                coverFileInfo.setMime(coverMime);
                coverFileInfo.setChecksum("");
                coverFileInfo.setStorage(StorageProvider.MINIO);
                fileInfoMapper.insert(coverFileInfo);

                StorageResponse uploadCoverResponse = storageContext.upload(coverInputStream, ossCoverName);
                if (uploadCoverResponse.isOk) {
                    coverFileInfo.setHash(uploadCoverResponse.hash);
                }
                fileInfoMapper.updateById(coverFileInfo);
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

                playlistMapper.updateById(playlist);
            } else {
                return ApiResult.fail("歌曲在歌单中已存在!");
            }
        }

        return ApiResult.ok();
    }

    public List<MusicInfo> getPlaylistMusic(Long playlistId, Integer page, Integer size) {
        PageParam pageParam = Helpers.buildPageParam(page, size);

        return playlistMapper.getPlaylistMusic(playlistId, pageParam);
    }

    public PlaylistVo getPlaylistInfo(Long playlistId) {
        return playlistMapper.getPlaylistInfo(playlistId);
    }

    public ApiResult<Void> updatePlaylist(PlaylistDTO playlistDTO) {
        Playlist playlist = playlistMapper.selectOne(new LambdaQueryWrapper<Playlist>().eq(Playlist::getId, playlistDTO.getId()));
        if (playlist == null) {
            return ApiResult.fail("歌单不存在");
        }

        MultipartFile coverFile = playlistDTO.getCoverFile();
        if (coverFile != null) {
            String coverMime = coverFile.getContentType();
            if (!FileUtil.checkMime(coverMime, FileUtil.FileType.IMAGE)) {
                return ApiResult.fail(String.format("cover file content type[%s] not support!", coverMime));
            }
            String ossCoverName = Constant.COVER_DIR + Helpers.buildOssFileName(coverFile.getOriginalFilename());
            FileInfo coverFileInfo = fileInfoMapper.selectOne(new LambdaQueryWrapper<FileInfo>().eq(FileInfo::getId, playlist.getCoverFileId()));
            try {
                if (coverFileInfo != null) {
                    storageContext.remove(coverFileInfo.getFilename());
                    fileInfoMapper.deleteById(coverFileInfo.getId());
                }
                coverFileInfo = new FileInfo();
                coverFileInfo.setOriginalFilename(coverFile.getOriginalFilename());
                coverFileInfo.setFilename(ossCoverName);
                coverFileInfo.setSize(coverFile.getSize());
                coverFileInfo.setMime(coverMime);
                coverFileInfo.setChecksum("");
                coverFileInfo.setStorage(StorageProvider.MINIO);
                fileInfoMapper.insert(coverFileInfo);

                StorageResponse uploadCoverResponse = storageContext.upload(coverFile.getInputStream(), ossCoverName);
                if (uploadCoverResponse.isOk) {
                    coverFileInfo.setHash(uploadCoverResponse.hash);
                }
                fileInfoMapper.updateById(coverFileInfo);
                playlist.setCoverFileId(coverFileInfo.getId());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        playlist.setId(playlistDTO.getId());
        playlist.setTitle(playlistDTO.getTitle());
        playlist.setAccessible(playlistDTO.getAccessible());
        playlist.setIntroduction(playlistDTO.getIntroduction());
        playlistMapper.updateById(playlist);

        return ApiResult.ok();
    }

    public List<PlaylistVo> getHomePlaylist(String key, Integer page, Integer size) {
        PageParam pageParam = Helpers.buildPageParam(page, size);
        key = StringUtils.hasText(key) ? Helpers.buildFuzzySearchParam(key) : null;
        return playlistMapper.getHomePlaylist(key, pageParam);
    }

    public ApiResult<String> removeMusic(Long playlistId, Long musicId) {
        Long userId = authContext.getUserId();
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
}

package org.beethoven.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.beethoven.mapper.MusicMapper;
import org.beethoven.mapper.MusicPlaylistMapper;
import org.beethoven.mapper.PlaylistMapper;
import org.beethoven.pojo.dto.AddPlaylistDto;
import org.beethoven.pojo.dto.MusicPlaylistDTO;
import org.beethoven.pojo.dto.PlaylistDTO;
import org.beethoven.pojo.entity.ApiResult;
import org.beethoven.pojo.entity.Music;
import org.beethoven.pojo.entity.MusicPlaylist;
import org.beethoven.pojo.entity.Playlist;
import org.beethoven.pojo.vo.PlaylistVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<PlaylistVo> getPlayList(PlaylistDTO playlistDTO) {
        int offset = (playlistDTO.getPage() - 1) * playlistDTO.getSize();
        return playlistMapper.getPlayList(offset, playlistDTO.getSize());
    }

    public void addPlaylist(@Valid AddPlaylistDto playlistInfo) {
        Playlist playlist = new Playlist();
        playlist.setCreator(authService.getUserId());
        playlist.setTitle(playlistInfo.getTitle());
        playlist.setIntroduction(playlistInfo.getIntroduction());

        playlistMapper.insert(playlist);
    }

    @Transactional
    public ApiResult<String> addMusicToPlaylist(@Valid MusicPlaylistDTO musicPlaylistDTO) {
        if (!musicMapper.exists(new LambdaQueryWrapper<Music>().eq(Music::getId, musicPlaylistDTO.getMusicId()))) {
            return ApiResult.fail("歌曲不存在！");
        }
        Playlist playlist = playlistMapper.selectOne(new LambdaQueryWrapper<Playlist>().eq(Playlist::getId, musicPlaylistDTO.getPlaylistId()));
        if (Objects.isNull(playlist)) {
            return ApiResult.fail("歌单不存在！");
        }
        if (musicPlaylistMapper.exists(
                new LambdaQueryWrapper<MusicPlaylist>()
                        .eq(MusicPlaylist::getMusicId, musicPlaylistDTO.getMusicId())
                        .eq(MusicPlaylist::getPlaylistId, musicPlaylistDTO.getPlaylistId()))) {
            return ApiResult.fail("歌单已存在该歌曲！");
        }
        MusicPlaylist musicPlaylist = new MusicPlaylist();
        musicPlaylist.setMusicId(musicPlaylistDTO.getMusicId());
        musicPlaylist.setPlaylistId(musicPlaylistDTO.getPlaylistId());

        musicPlaylistMapper.insert(musicPlaylist);

        playlist.setMusicCount(playlist.getMusicCount() + 1);
        playlistMapper.updateById(playlist);

        return ApiResult.ok();
    }
}

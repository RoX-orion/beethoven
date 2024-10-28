package org.beethoven.service;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.beethoven.entity.Playlist;
import org.beethoven.mapper.PlaylistMapper;
import org.beethoven.pojo.PageInfo;
import org.beethoven.pojo.dto.AddPlaylistDto;
import org.beethoven.pojo.dto.PlaylistDTO;
import org.beethoven.pojo.vo.PlaylistVo;
import org.springframework.stereotype.Service;

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
    private AuthService authService;

    public PageInfo<PlaylistVo> getPlayList(PlaylistDTO playlistDTO) {
        return null;
    }

    public void addPlaylist(@Valid AddPlaylistDto playlistInfo) {
        Playlist playlist = new Playlist();
        playlist.setCreator(authService.getUserId());
        playlist.setTitle(playlistInfo.getTitle());
        playlist.setIntroduction(playlistInfo.getIntroduction());

        playlistMapper.insert(playlist);
    }
}

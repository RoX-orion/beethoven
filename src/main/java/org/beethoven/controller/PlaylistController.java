package org.beethoven.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.beethoven.pojo.dto.AddPlaylistDto;
import org.beethoven.pojo.dto.PlaylistDTO;
import org.beethoven.pojo.entity.ApiResult;
import org.beethoven.pojo.vo.PlaylistVo;
import org.beethoven.service.PlaylistService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-10-20
 */

@RestController
@RequestMapping("playlist")
public class PlaylistController {

    @Resource
    private PlaylistService playlistService;

    @RequestMapping(value = "getPlaylist", method = RequestMethod.GET)
    public ApiResult<List<PlaylistVo>> getPlayList(PlaylistDTO playlistDTO) {
        List<PlaylistVo> playList = playlistService.getPlayList(playlistDTO);

        return ApiResult.ok(playList);
    }

    @RequestMapping(value = "addPlaylist", method = RequestMethod.POST)
    public ApiResult<Void> addPlaylist(@RequestBody @Valid AddPlaylistDto playlistInfo) {
        playlistService.addPlaylist(playlistInfo);

        return ApiResult.ok();
    }
}

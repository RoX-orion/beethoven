package org.beethoven.controller;

import org.beethoven.entity.ApiResult;
import org.beethoven.pojo.PageInfo;
import org.beethoven.pojo.dto.PlaylistDTO;
import org.beethoven.pojo.vo.PlaylistVo;
import org.beethoven.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    private final PlaylistService playlistService;

    @Autowired
    private PlaylistController(
            final PlaylistService playlistService
    ) {
        this.playlistService = playlistService;
    }

    @RequestMapping(value = "getPlaylist", method = RequestMethod.GET)
    public ApiResult<PageInfo<PlaylistVo>> getPlayList(PlaylistDTO playlistDTO) {
        PageInfo<PlaylistVo> pageInfo = playlistService.getPlayList(playlistDTO);

        return ApiResult.ok(pageInfo);
    }
}

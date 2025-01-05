package org.beethoven.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.beethoven.pojo.dto.MusicPlaylistDTO;
import org.beethoven.pojo.dto.PlaylistDTO;
import org.beethoven.pojo.entity.ApiResult;
import org.beethoven.pojo.vo.MusicVo;
import org.beethoven.pojo.vo.PlaylistVo;
import org.beethoven.service.PlaylistService;
import org.springframework.web.bind.annotation.*;

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
    public ApiResult<Void> addPlaylist(@RequestBody @Valid PlaylistDTO playlistInfo) {
        playlistService.addPlaylist(playlistInfo);

        return ApiResult.ok();
    }

    @RequestMapping(value = "addMusic", method = RequestMethod.POST)
    public ApiResult<String> addMusicToPlaylist(@RequestBody @Valid MusicPlaylistDTO musicPlaylistDTO) {
        return playlistService.addMusicToPlaylist(musicPlaylistDTO);
    }

    @RequestMapping(value = "music", method = RequestMethod.GET)
    public ApiResult<List<MusicVo>> getPlaylistMusic(@RequestParam("playlistId") Long playlistId,
                                                     @RequestParam(value = "page", required = false) Integer page,
                                                     @RequestParam(value = "size", required = false) Integer size) {
        List<MusicVo> playListMusic = playlistService.getPlaylistMusic(playlistId, page, size);

        return ApiResult.ok(playListMusic);
    }

    @RequestMapping(value = "info", method = RequestMethod.GET)
    public ApiResult<PlaylistVo> getPlaylistInfo(@RequestParam("playlistId") Long playlistId) {
        PlaylistVo playlistVo = playlistService.getPlaylistInfo(playlistId);

        return ApiResult.ok(playlistVo);
    }

    @RequestMapping(value = "updatePlaylist", method = RequestMethod.PUT)
    public ApiResult<Void> updatePlaylist(@RequestBody @Valid PlaylistDTO playlistDTO) {
        return playlistService.updatePlaylist(playlistDTO);
    }
}

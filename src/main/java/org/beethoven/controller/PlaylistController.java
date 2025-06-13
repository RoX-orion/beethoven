package org.beethoven.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.beethoven.lib.annotation.Permission;
import org.beethoven.pojo.dto.MusicPlaylistDTO;
import org.beethoven.pojo.dto.PlaylistDTO;
import org.beethoven.pojo.entity.ApiResult;
import org.beethoven.pojo.vo.MusicInfo;
import org.beethoven.pojo.vo.PlaylistVo;
import org.beethoven.service.PlaylistService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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

    @Permission
    @RequestMapping(value = "getPlaylist", method = RequestMethod.GET)
    public ApiResult<List<PlaylistVo>> getSelfPlayList(PlaylistDTO playlistDTO) {
        List<PlaylistVo> playList = playlistService.getSelfPlayList(playlistDTO);

        return ApiResult.ok(playList);
    }

    @Permission
    @RequestMapping(value = "addPlaylist", method = RequestMethod.POST)
    public ApiResult<String> addPlaylist(@Valid PlaylistDTO playlistInfo) throws IOException {
       return playlistService.addPlaylist(playlistInfo);
    }

    @Permission
    @RequestMapping(value = "addMusic", method = RequestMethod.POST)
    public ApiResult<String> addMusicToPlaylist(@RequestBody @Valid MusicPlaylistDTO musicPlaylistDTO) {
        return playlistService.addMusicToPlaylist(musicPlaylistDTO);
    }

    @RequestMapping(value = "music", method = RequestMethod.GET)
    public ApiResult<List<MusicInfo>> getPlaylistMusic(@RequestParam("playlistId") Long playlistId,
                                                       @RequestParam(value = "page", required = false) Integer page,
                                                       @RequestParam(value = "size", required = false) Integer size) {
        List<MusicInfo> playListMusic = playlistService.getPlaylistMusic(playlistId, page, size);

        return ApiResult.ok(playListMusic);
    }

    @RequestMapping(value = "info", method = RequestMethod.GET)
    public ApiResult<PlaylistVo> getPlaylistInfo(@RequestParam("playlistId") Long playlistId) {
        PlaylistVo playlistVo = playlistService.getPlaylistInfo(playlistId);

        return ApiResult.ok(playlistVo);
    }

    @Permission
    @RequestMapping(value = "updatePlaylist", method = RequestMethod.PUT)
    public ApiResult<Void> updatePlaylist(@Valid PlaylistDTO playlistDTO) {
        return playlistService.updatePlaylist(playlistDTO);
    }

    @RequestMapping(value = "home/playlist", method = RequestMethod.GET)
    public ApiResult<List<PlaylistVo>> getHomePlaylist(@RequestParam(value = "key", required = false) String key,
                                                       @RequestParam(value = "page") Integer page,
                                                       @RequestParam(value = "size") Integer size) {
        List<PlaylistVo> playlistVoList = playlistService.getHomePlaylist(key, page, size);
        return ApiResult.ok(playlistVoList);
    }

    @Permission
    @RequestMapping(value = "removeMusic/{playlistId}/{musicId}", method = RequestMethod.DELETE)
    public ApiResult<String> removeMusic(@PathVariable("playlistId") Long playlistId,
                                         @PathVariable("musicId") Long musicId) {
        return playlistService.removeMusic(playlistId, musicId);
    }
}

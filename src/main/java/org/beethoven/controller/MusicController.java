package org.beethoven.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.beethoven.lib.annotation.Permission;
import org.beethoven.pojo.PageInfo;
import org.beethoven.pojo.dto.SearchDTO;
import org.beethoven.pojo.dto.UpdateMusicDTO;
import org.beethoven.pojo.dto.UploadMusicDTO;
import org.beethoven.pojo.entity.ApiResult;
import org.beethoven.pojo.vo.MusicManagement;
import org.beethoven.pojo.vo.MusicInfo;
import org.beethoven.service.MusicService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-10-09
 */

@RestController
@RequestMapping("music")
public class MusicController {

    @Resource
    private MusicService musicService;

    @Permission
    @RequestMapping(value = "uploadMusic", method = RequestMethod.POST, consumes = "multipart/form-data")
    public ApiResult<String> uploadMusic(@Valid UploadMusicDTO uploadMusicDTO) throws IOException {
        return musicService.uploadMusic(uploadMusicDTO);
    }

    @Permission
    @RequestMapping(value = "fetchMusic", method = RequestMethod.GET)
    public ApiResult<Void> fetchMusic(HttpServletRequest request,
                                      HttpServletResponse response,
                                      @RequestParam("fileName") String fileName) {
        musicService.fetchMusic(request, response, fileName);

        return ApiResult.ok();
    }

    @RequestMapping(value = "searchMusic", method = RequestMethod.GET)
    public ApiResult<List<MusicInfo>> searchMusic(@Valid SearchDTO searchDTO) {
        List<MusicInfo> musicInfoList = musicService.searchMusic(searchDTO);

        return ApiResult.ok(musicInfoList);
    }

    @RequestMapping(value = "info/{id}", method = RequestMethod.GET)
    public ApiResult<MusicInfo> getMusicInfo(@PathVariable Long id) {
        MusicInfo musicInfo = musicService.getMusicInfo(id);

        return ApiResult.ok(musicInfo);
    }

    @Permission
    @RequestMapping(value = "manage/getManageMusicList", method = RequestMethod.GET)
    public ApiResult<PageInfo<MusicManagement>> getManageMusicList(SearchDTO searchDTO) {
        PageInfo<MusicManagement> pageInfo = musicService.getManageMusicList(searchDTO);

        return ApiResult.ok(pageInfo);
    }

    @Permission
    @RequestMapping(value = "manage/deleteMusic/{musicId}", method = RequestMethod.DELETE)
    public ApiResult<String> deleteMusic(@PathVariable("musicId") Long musicId) {
        return musicService.deleteMusic(musicId);
    }

    @Permission
    @RequestMapping(value = "manage/updateMusic", method = RequestMethod.PUT)
    public ApiResult<String> updateMusic(@Valid UpdateMusicDTO updateMusicDTO) throws IOException {
        return musicService.updateMusic(updateMusicDTO);
    }
}

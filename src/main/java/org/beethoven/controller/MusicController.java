package org.beethoven.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.beethoven.pojo.PageInfo;
import org.beethoven.pojo.dto.MusicDTO;
import org.beethoven.pojo.dto.UpdateMusicDTO;
import org.beethoven.pojo.dto.UploadMusicDTO;
import org.beethoven.pojo.entity.ApiResult;
import org.beethoven.pojo.vo.ManageMusic;
import org.beethoven.pojo.vo.MusicVo;
import org.beethoven.service.MusicService;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = "uploadMusic", method = RequestMethod.POST, consumes = "multipart/form-data")
    public ApiResult<String> uploadMusic(@Valid UploadMusicDTO uploadMusicDTO) {
        return musicService.uploadMusic(uploadMusicDTO);
    }

    @RequestMapping(value = "fetchMusic", method = RequestMethod.GET)
    public ApiResult<Void> fetchMusic(HttpServletRequest request,
                                      HttpServletResponse response,
                                      @RequestParam("fileName") String fileName) {
        musicService.fetchMusic(request, response, fileName);

        return ApiResult.ok();
    }

    @RequestMapping(value = "searchMusic", method = RequestMethod.GET)
    public ApiResult<List<MusicVo>> searchMusic(@Valid MusicDTO musicDTO) {
        List<MusicVo> musicVoList = musicService.searchMusic(musicDTO);

        return ApiResult.ok(musicVoList);
    }

    @RequestMapping(value = "info/{id}", method = RequestMethod.GET)
    public ApiResult<MusicVo> getMusicInfo(@PathVariable Long id) {
        MusicVo musicInfo = musicService.getMusicInfo(id);

        return ApiResult.ok(musicInfo);
    }

    @RequestMapping(value = "manage/getManageMusicList", method = RequestMethod.GET)
    public ApiResult<PageInfo<List<ManageMusic>>> getManageMusicList(MusicDTO musicDTO) {
        PageInfo<List<ManageMusic>> pageInfo = musicService.getManageMusicList(musicDTO);

        return ApiResult.ok(pageInfo);
    }

    @RequestMapping(value = "manage/deleteMusic/{musicId}", method = RequestMethod.DELETE)
    public ApiResult<String> deleteMusic(@PathVariable("musicId") Long musicId) {
        return musicService.deleteMusic(musicId);
    }

    @RequestMapping(value = "manage/updateMusic", method = RequestMethod.PUT)
    public ApiResult<String> updateMusic(@Valid UpdateMusicDTO updateMusicDTO) {
        return musicService.updateMusic(updateMusicDTO);
    }
}

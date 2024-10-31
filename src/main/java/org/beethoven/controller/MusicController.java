package org.beethoven.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.beethoven.pojo.dto.MusicDTO;
import org.beethoven.pojo.entity.ApiResult;
import org.beethoven.pojo.vo.MusicVo;
import org.beethoven.service.MusicService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @RequestMapping("uploadMusic")
    public ApiResult<String> uploadMusic(@RequestParam("file") MultipartFile file) {
        musicService.uploadMusic(file);

        return ApiResult.ok();
    }

    @RequestMapping(value = "fetchMusic", method = RequestMethod.GET)
    public ApiResult<Void> fetchMusic(HttpServletRequest request,
                                      @RequestParam("hash") String hash) {
        musicService.fetchMusic(request, hash);

        return ApiResult.ok();
    }

    @RequestMapping(value = "searchMusic", method = RequestMethod.GET)
    public ApiResult<List<MusicVo>> searchMusic(@Valid MusicDTO musicDTO) {
        List<MusicVo> musicVoList = musicService.searchMusic(musicDTO);

        return ApiResult.ok(musicVoList);
    }
}

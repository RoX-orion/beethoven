package org.beethoven.controller;

import jakarta.annotation.Resource;
import org.beethoven.lib.annotation.Permission;
import org.beethoven.pojo.PageInfo;
import org.beethoven.pojo.dto.SearchDTO;
import org.beethoven.pojo.entity.ApiResult;
import org.beethoven.pojo.vo.VideoManagement;
import org.beethoven.pojo.vo.VideoVo;
import org.beethoven.service.VideoService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Copyright (c) 2025 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2025-05-11
 */

@RestController
@RequestMapping("video")
public class VideoController {

    @Resource
    private VideoService videoService;

    @RequestMapping(value = "info/{videoId}", method = RequestMethod.GET)
    public ApiResult<VideoVo> getVideoInfo(@PathVariable("videoId") Long videoId) {
        VideoVo videoVo = videoService.getVideoInfo(videoId);

        return ApiResult.ok(videoVo);
    }

    @Permission
    @RequestMapping(value = "manage/getManageVideoList", method = RequestMethod.GET)
    public ApiResult<PageInfo<VideoManagement>> getManageVideoList(SearchDTO searchDTO) {
        PageInfo<VideoManagement> videoManagementList = videoService.getManageVideoList(searchDTO);

        return ApiResult.ok(videoManagementList);
    }
}

package org.beethoven.controller;

import jakarta.annotation.Resource;
import org.beethoven.pojo.PageInfo;
import org.beethoven.pojo.dto.SearchDTO;
import org.beethoven.pojo.entity.ApiResult;
import org.beethoven.pojo.vo.AlbumManagement;
import org.beethoven.service.AlbumService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Copyright (c) 2025 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2025-05-10
 */

@RestController
@RequestMapping("album")
public class AlbumController {

    @Resource
    private AlbumService albumService;

    @RequestMapping(value = "manage/getManageAlbumList", method = RequestMethod.GET)
    public ApiResult<PageInfo<List<AlbumManagement>>> getManageMusicList(SearchDTO searchDTO) {
        PageInfo<List<AlbumManagement>> pageInfo = albumService.getManageAlbumList(searchDTO);

        return ApiResult.ok(pageInfo);
    }
}

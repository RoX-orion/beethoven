package org.beethoven.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.beethoven.lib.annotation.Permission;
import org.beethoven.pojo.PageInfo;
import org.beethoven.pojo.dto.AlbumDTO;
import org.beethoven.pojo.dto.SearchDTO;
import org.beethoven.pojo.entity.ApiResult;
import org.beethoven.pojo.vo.AlbumManagement;
import org.beethoven.service.AlbumService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    @Permission
    @RequestMapping(value = "manage/getManageAlbumList", method = RequestMethod.GET)
    public ApiResult<PageInfo<AlbumManagement>> getManageMusicList(SearchDTO searchDTO) {
        PageInfo<AlbumManagement> pageInfo = albumService.getManageAlbumList(searchDTO);

        return ApiResult.ok(pageInfo);
    }

    @Permission
    @RequestMapping(value = "addAlbum", method = RequestMethod.POST)
    public ApiResult<Void> addAlbum(@RequestBody @Valid AlbumDTO albumDTO) {
        return albumService.addAlbum(albumDTO);
    }
}

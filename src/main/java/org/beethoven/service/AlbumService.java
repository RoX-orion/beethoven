package org.beethoven.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.beethoven.lib.AuthContext;
import org.beethoven.mapper.AlbumMapper;
import org.beethoven.pojo.PageInfo;
import org.beethoven.pojo.dto.AlbumDTO;
import org.beethoven.pojo.dto.SearchDTO;
import org.beethoven.pojo.entity.Album;
import org.beethoven.pojo.entity.ApiResult;
import org.beethoven.pojo.vo.AlbumManagement;
import org.beethoven.util.Helpers;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Copyright (c) 2025 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2025-05-10
 */

@Service
public class AlbumService {

    @Resource
    private AlbumMapper albumMapper;

    @Resource
    private AuthContext authContext;


    public PageInfo<AlbumManagement> getManageAlbumList(SearchDTO searchDTO) {
        int offset = (searchDTO.getPage() - 1) * searchDTO.getSize();
        String key = Helpers.buildFuzzySearchParam(searchDTO.getKey());
        List<AlbumManagement> albumManagementList = albumMapper.getManageAlbumList(offset, searchDTO.getSize(), key);

        LambdaQueryWrapper<Album> queryWrapper = new QueryWrapper<Album>().lambda();
        if (StringUtils.hasText(key)) {
            queryWrapper = new QueryWrapper<Album>().lambda().like(Album::getName, key);
        }
        Long total = albumMapper.selectCount(queryWrapper);

        return PageInfo.result(albumManagementList, total);
    }

    public ApiResult<Void> addAlbum(@Valid AlbumDTO albumDTO) {
        if (albumMapper.exists(new LambdaQueryWrapper<Album>().eq(Album::getName, albumDTO.getName().trim()))) {
            return ApiResult.fail("专辑已存在！");
        }
        Album album = new Album();
        album.setCreator(authContext.getUserId());
        album.setName(albumDTO.getName().trim());
        albumMapper.insert(album);

        return ApiResult.ok();
    }
}

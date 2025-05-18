package org.beethoven.service;

import jakarta.annotation.Resource;
import org.beethoven.mapper.VideoMapper;
import org.beethoven.pojo.PageInfo;
import org.beethoven.pojo.dto.SearchDTO;
import org.beethoven.pojo.vo.VideoManagement;
import org.beethoven.pojo.vo.VideoVo;
import org.beethoven.util.Helpers;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright (c) 2025 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2025-05-11
 */

@Service
public class VideoService {

    @Resource
    private VideoMapper videoMapper;

    public VideoVo getVideoInfo(Long videoId) {
        return videoMapper.getVideoInfo(videoId);
    }

    public PageInfo<VideoManagement> getManageVideoList(SearchDTO searchDTO) {
        int offset = (searchDTO.getPage() - 1) * searchDTO.getSize();
        String key = Helpers.buildFuzzySearchParam(searchDTO.getKey());
        List<VideoManagement> videoManagementList = videoMapper.getManageVideoList(offset, searchDTO.getSize(), key);
        long total = videoMapper.getManageVideoCount(key);

        return PageInfo.result(videoManagementList, total);
    }
}

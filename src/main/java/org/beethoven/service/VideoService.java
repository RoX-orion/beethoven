package org.beethoven.service;

import jakarta.annotation.Resource;
import org.beethoven.mapper.VideoMapper;
import org.beethoven.pojo.vo.VideoVo;
import org.springframework.stereotype.Service;

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
}

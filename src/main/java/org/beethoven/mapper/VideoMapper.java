package org.beethoven.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.beethoven.pojo.entity.Video;
import org.beethoven.pojo.vo.VideoVo;

/**
 * Copyright (c) 2025 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2025-05-11
 */

public interface VideoMapper extends BaseMapper<Video> {

    VideoVo getVideoInfo(@Param("videoId") Long videoId);
}

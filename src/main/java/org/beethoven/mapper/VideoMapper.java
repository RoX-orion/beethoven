package org.beethoven.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.beethoven.pojo.entity.Video;
import org.beethoven.pojo.vo.VideoManagement;
import org.beethoven.pojo.vo.VideoVo;

import java.util.List;

/**
 * Copyright (c) 2025 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2025-05-11
 */

public interface VideoMapper extends BaseMapper<Video> {

    VideoVo getVideoInfo(@Param("videoId") Long videoId);

    List<VideoManagement> getManageVideoList(@Param("offset") int offset,
                                             @Param("size") Integer size,
                                             @Param("key") String key);

    long getManageVideoCount(@Param("key") String key);
}

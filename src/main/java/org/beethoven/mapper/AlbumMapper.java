package org.beethoven.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.beethoven.pojo.entity.Album;
import org.beethoven.pojo.vo.AlbumManagement;

import java.util.List;

/**
 * Copyright (c) 2025 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2025-05-10
 */

public interface AlbumMapper extends BaseMapper<Album> {

    List<AlbumManagement> getManageAlbumList(@Param("offset") int offset,
                                             @Param("size") Integer size,
                                             @Param("key") String key);
}

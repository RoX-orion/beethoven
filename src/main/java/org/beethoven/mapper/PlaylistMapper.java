package org.beethoven.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Param;
import org.beethoven.pojo.entity.Playlist;
import org.beethoven.pojo.vo.PlaylistVo;

import java.util.List;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-10-28
 */

public interface PlaylistMapper extends BaseMapper<Playlist> {
    List<PlaylistVo> getPlayList(@Param("offset") int offset,
                                 @NotNull @Param("size") Integer size);
}

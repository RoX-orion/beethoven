package org.beethoven.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Param;
import org.beethoven.pojo.entity.Music;
import org.beethoven.pojo.vo.MusicVo;

import java.util.List;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-10-22
 */

public interface MusicMapper extends BaseMapper<Music> {

    List<MusicVo> searchMusic(@Param("offset") int offset,
                              @NotNull @Param("size") Integer size,
                              @NotBlank @Param("key") String key);
}

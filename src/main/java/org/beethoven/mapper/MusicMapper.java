package org.beethoven.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.beethoven.pojo.entity.Music;
import org.beethoven.pojo.vo.MusicManagement;
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
                              @Param("size") Integer size,
                              @Param("key") String key);

    List<MusicManagement> getManageMusicList(@Param("offset") int offset,
                                             @Param("size") Integer size,
                                             @Param("key") String key);
}

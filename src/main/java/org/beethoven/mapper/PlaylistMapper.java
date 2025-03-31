package org.beethoven.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Param;
import org.beethoven.pojo.PageParam;
import org.beethoven.pojo.entity.Playlist;
import org.beethoven.pojo.vo.MusicVo;
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
    List<PlaylistVo> getSelfPlayList(@Param("offset") int offset,
                                     @NotNull @Param("size") Integer size,
                                     @Param("userId") Long userId);

    List<MusicVo> getPlaylistMusic(@Param("playlistId") Long playlistId,
                                   @Param("pageParam") PageParam pageParam);

    PlaylistVo getPlaylistInfo(@Param("playlistId") Long playlistId);

    List<PlaylistVo> getHomePlaylist(@Param("key") String key,
                                     @Param("pageParam") PageParam pageParam);
}

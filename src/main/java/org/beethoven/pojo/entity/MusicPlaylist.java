package org.beethoven.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-12-25
 */

@Data
@TableName("music_playlist")
public class MusicPlaylist {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long musicId;

    private Long playlistId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

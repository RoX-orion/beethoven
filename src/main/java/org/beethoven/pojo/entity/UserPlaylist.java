package org.beethoven.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Copyright (c) 2025 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2025-03-31
 */

@Data
@TableName("user_playlist")
public class UserPlaylist {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long accountId;

    private Long playlistId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

package org.beethoven.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-10-20
 */

@Data
@TableName("playlist")
public class Playlist {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long creator;

    private String title;

    private String introduction;

    private Integer musicCount;

    private boolean accessible;

    private Integer sequence;

    private Long coverFileId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

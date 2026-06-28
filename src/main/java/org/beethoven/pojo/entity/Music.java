package org.beethoven.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-10-14
 */

@Data
@TableName("music")
public class Music {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String name;

    private String singer;

    private String album;

    private Integer duration;

    private String musicFileId;

    private String coverFileId;

    private String hash;

    private String sha;

    private Integer shardingSize;

    private String videoId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

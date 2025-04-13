package org.beethoven.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.beethoven.pojo.enums.StorageProvider;

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
    private Long id;

    private String name;

    private String singer;

    private String album;

    private int duration;

    private long size;

    private String mime;

    private Long musicFileId;

    private Long coverFileId;

//    private String state;

    private String hash;

    private String sha;

    private StorageProvider storage;

    private int shardingSize;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

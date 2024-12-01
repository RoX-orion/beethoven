package org.beethoven.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.beethoven.lib.Constant;
import org.beethoven.lib.annotation.SplicingValue;
import org.beethoven.pojo.enums.StorageProvider;

import java.util.Date;

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

    private int duration;

    private long size;

    private String mime;

    @SplicingValue(Constant.ENDPOINT_PREFIX)
    private String ossMusicName;

    @SplicingValue(Constant.ENDPOINT_PREFIX)
    private String ossCoverName;

//    private String state;

    private String hash;

    private String sha;

    private StorageProvider storage;

    private int shardingSize;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}

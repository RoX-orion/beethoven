package org.beethoven.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.beethoven.pojo.enums.OssProvider;

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

    @TableId
    private Integer id;

    private String name;

    private String singer;

    private String album;

    private int duration;

    private long size;

    private String mime;

    private String ossMusicName;

    private String ossCoverName;

//    private String state;

    private String hash;

    private String sha;

    private OssProvider oss;

    private int shardingSize;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}

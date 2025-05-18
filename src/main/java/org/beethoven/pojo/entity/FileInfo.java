package org.beethoven.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.beethoven.lib.Constant;
import org.beethoven.lib.annotation.SplicingValue;
import org.beethoven.pojo.enums.StorageProvider;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Copyright (c) 2025 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2025-04-08
 */

@Data
@TableName("file_info")
public class FileInfo {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String originalFilename;

    @SplicingValue(Constant.ENDPOINT_PREFIX)
    private String filename;

    private long size;

    private String mime;

    private String checksum;

    private StorageProvider storage;

    private String hash;

    private List<String> hashList;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

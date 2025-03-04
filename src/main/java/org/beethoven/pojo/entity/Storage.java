package org.beethoven.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.beethoven.pojo.enums.StorageProvider;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-11-30
 */

@Data
@TableName("storage")
public class Storage {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private StorageProvider provider;

    private String accessKey;

    private String secretKey;

    private String bucket;

    private String endpoint;
}

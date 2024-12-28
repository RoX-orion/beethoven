package org.beethoven.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-11-03
 */

@Data
@TableName("config")
public class Config {

    @TableId(type = IdType.ASSIGN_ID)
    private String configKey;

    private String configValue;

    private String remark;
}

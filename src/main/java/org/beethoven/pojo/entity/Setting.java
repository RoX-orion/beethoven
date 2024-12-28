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
 * @date: 2024-10-20
 */

@Data
@TableName("setting")
public class Setting {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Integer userId;

    private Boolean isMute;

    private int volume;
}

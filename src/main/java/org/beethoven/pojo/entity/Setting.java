package org.beethoven.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
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

    @TableId
    private Long userId;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private Boolean isMute;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private int volume;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long musicId;

    @TableField(value = "\"current_time\"", updateStrategy = FieldStrategy.NOT_EMPTY)
    private int currentTime;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private String playMode;
}

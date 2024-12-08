package org.beethoven.pojo.entity;

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
    private Integer id;

    private Integer userId;

    private Boolean isMute;

    private int volume;

    @TableField(exist = false)
    private String defaultMusicCover;
}

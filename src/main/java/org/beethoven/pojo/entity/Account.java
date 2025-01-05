package org.beethoven.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.beethoven.lib.Constant;
import org.beethoven.lib.annotation.SplicingValue;
import org.beethoven.pojo.enums.UserType;

import java.time.LocalDateTime;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-11-13
 */

@Data
@TableName("account")
public class Account {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String email;

    private String username;

    private UserType userType;

    @SplicingValue(Constant.ENDPOINT_PREFIX)
    private String avatar;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

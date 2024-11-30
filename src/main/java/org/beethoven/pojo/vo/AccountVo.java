package org.beethoven.pojo.vo;

import lombok.Data;
import org.beethoven.lib.Constant;
import org.beethoven.lib.annotation.SplicingValue;

import java.time.LocalDateTime;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-11-13
 */

@Data
public class AccountVo {

    private Integer id;

    private String username;

    @SplicingValue(Constant.ENDPOINT_PREFIX)
    private String avatar;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

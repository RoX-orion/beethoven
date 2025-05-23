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
 * @date: 2024-10-21
 */

@Data
public class PlaylistVo {

    private Long id;

    private String author;

    private String title;

    @SplicingValue(Constant.ENDPOINT_PREFIX)
    private String cover;

    private int musicCount;

    private boolean accessible;

    private String introduction;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

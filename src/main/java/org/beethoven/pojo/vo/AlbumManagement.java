package org.beethoven.pojo.vo;

import lombok.Data;
import org.beethoven.lib.Constant;
import org.beethoven.lib.annotation.SplicingValue;

import java.time.LocalDateTime;

/**
 * Copyright (c) 2025 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2025-05-10
 */

@Data
public class AlbumManagement {

    private Long id;

    private String name;

    private String creator;

    @SplicingValue(Constant.ENDPOINT_PREFIX)
    private String cover;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

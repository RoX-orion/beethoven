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
 * @date: 2025-05-17
 */

@Data
public class VideoManagement {

    private Long videoId;

    @SplicingValue(Constant.ENDPOINT_PREFIX)
    private String link;

    @SplicingValue(Constant.ENDPOINT_PREFIX)
    private String cover;

    private String name;

    private int duration;

    private int size;

    private String mime;

    private String singer;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

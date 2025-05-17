package org.beethoven.pojo.vo;

import lombok.Data;
import org.beethoven.lib.Constant;
import org.beethoven.lib.annotation.SplicingValue;

/**
 * Copyright (c) 2025 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2025-05-11
 */

@Data
public class VideoVo {

    private Long videoId;

    @SplicingValue(Constant.ENDPOINT_PREFIX)
    public String link;
}

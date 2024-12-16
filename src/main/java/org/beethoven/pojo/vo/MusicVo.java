package org.beethoven.pojo.vo;

import lombok.Data;
import org.beethoven.lib.Constant;
import org.beethoven.lib.annotation.SplicingValue;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-10-10
 */

@Data
public class MusicVo {

    public String id;

    public String name;

//    @SplicingValue(Constant.ENDPOINT_PREFIX)
    public String link;

    @SplicingValue(Constant.ENDPOINT_PREFIX)
    public String cover;

    public String singer;

    public int duration;

    public int size;

    public String mime;
}

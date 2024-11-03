package org.beethoven.pojo.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-11-03
 */

public enum OssProvider {

    QINIU(0, "七牛");

    @EnumValue
    private int provider;

    private String name;

    OssProvider(int provider, String name) {
        this.provider = provider;
        this.name = name;
    }
}

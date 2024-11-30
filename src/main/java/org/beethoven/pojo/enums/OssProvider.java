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

    QINIU("qiniu", "七牛云"),
    MINIO("minio", "Minio");

    @EnumValue
    private final String provider;

    private final String name;

    OssProvider(String provider, String name) {
        this.provider = provider;
        this.name = name;
    }
}

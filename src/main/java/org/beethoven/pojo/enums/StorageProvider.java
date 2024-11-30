package org.beethoven.pojo.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;
import org.beethoven.lib.exception.StorageException;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-11-03
 */

@Getter
public enum StorageProvider implements IEnum<String> {

    QINIU("七牛云"),
    MINIO("MinIO");

    private final String name;

    StorageProvider(String name) {
        this.name = name;
    }

    public static StorageProvider getProvider(String provider) {
        for (StorageProvider value : StorageProvider.values()) {
            if (value.name.equals(provider)) {
                return value;
            }
        }
        throw new StorageException("Not found oss provider!");
    }

    public static boolean contains(String provider) {
        for (StorageProvider value : StorageProvider.values()) {
            if (value.name.equals(provider)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getValue() {
        return this.name();
    }
}

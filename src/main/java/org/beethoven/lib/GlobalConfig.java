package org.beethoven.lib;

import lombok.Setter;
import org.beethoven.lib.exception.StorageException;
import org.beethoven.pojo.entity.StorageInfo;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-11-03
 */

public class GlobalConfig {

    public static int shardingSize = Constant.DEFAULT_SHARDING_SIZE;

    public static String defaultMusicCover;

    @Setter
    private static StorageInfo storageInfo;

    public static String endpoint;

    public static StorageInfo getStorageInfo() {
        if (storageInfo == null) {
            throw new StorageException("StorageInfo config is null!");
        }
        return storageInfo;
    }

    public static String getValue() {
        return null;
    }
}

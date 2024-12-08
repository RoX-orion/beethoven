package org.beethoven.lib;

import lombok.Setter;
import org.beethoven.lib.exception.StorageException;
import org.beethoven.pojo.entity.Storage;

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
    private static Storage storage;

    public static String endpoint;

    public static Storage getStorage() {
        if (storage == null) {
            throw new StorageException("Storage config is null!");
        }
        return storage;
    }

    public static String getValue() {
        return null;
    }
}

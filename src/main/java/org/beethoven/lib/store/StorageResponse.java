package org.beethoven.lib.store;

import lombok.Data;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-11-26
 */

@Data
public class StorageResponse {

    public boolean isOk;

    public String hash;
}

package org.beethoven.lib.store;

import java.io.InputStream;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-11-21
 */

public interface Storage {

    StorageResponse upload(InputStream inputStream, String bucket, String fileName);

    void download();
}

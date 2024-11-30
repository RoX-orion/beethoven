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

    void init();

    StorageResponse upload(InputStream inputStream, String fileName);

    void download();

    String getURL(String fileName);
}

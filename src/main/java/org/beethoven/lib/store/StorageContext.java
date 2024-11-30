package org.beethoven.lib.store;

import jakarta.annotation.Resource;
import org.beethoven.pojo.enums.StorageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-11-25
 */

@Component
public class StorageContext implements Storage {

    @Resource
    private ApplicationContext applicationContext;

    private Storage storage;

    private final Map<String, Storage> ossMap = new HashMap<>();

    @Autowired
    public StorageContext(final Qiniu qiniu,
                          final MinIO minIO) {
        ossMap.put(StorageProvider.QINIU.name(), qiniu);
        ossMap.put(StorageProvider.MINIO.name(), minIO);
    }

    public void refresh(String provider) {
        storage = ossMap.get(provider) != null ? ossMap.get(provider) : applicationContext.getBean(MinIO.class);
        init();
    }

    @Override
    public void init() {
        storage.init();
    }

    @Override
    public StorageResponse upload(InputStream inputStream, String fileName) {
        return storage.upload(inputStream, fileName);
    }

    @Override
    public void download() {
        storage.download();
    }

    @Override
    public String getURL(String fileName) {
        return storage.getURL(fileName);
    }
}

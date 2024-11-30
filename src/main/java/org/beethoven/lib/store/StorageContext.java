package org.beethoven.lib.store;

import jakarta.annotation.Resource;
import org.beethoven.pojo.enums.OssProvider;
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

    private final Map<OssProvider, Storage> ossMap = new HashMap<>();

    @Autowired
    public StorageContext(final Qiniu qiniu,
                          final MinIO minIO) {
        ossMap.put(OssProvider.QINIU, qiniu);
        ossMap.put(OssProvider.MINIO, minIO);
    }

    public void refresh(OssProvider ossProvider) {
        storage = ossMap.get(ossProvider) != null ? ossMap.get(ossProvider) : applicationContext.getBean(MinIO.class);
    }

    @Override
    public StorageResponse upload(InputStream inputStream, String bucket, String fileName) {
        return storage.upload(inputStream, bucket, fileName);
    }

    @Override
    public void download() {
        storage.download();
    }
}

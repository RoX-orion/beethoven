package org.beethoven.lib.store;

import jakarta.annotation.Resource;
import lombok.Getter;
import org.beethoven.lib.GlobalConfig;
import org.beethoven.lib.exception.BeethovenException;
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
public class StorageContext {

    @Resource
    private ApplicationContext applicationContext;

    private Storage storage;

    @Getter
    private StorageProvider provider;

    private final Map<StorageProvider, Storage> ossMap = new HashMap<>();

    @Autowired
    public StorageContext(final Qiniu qiniu,
                          final S3 s3) {
        ossMap.put(StorageProvider.QINIU, qiniu);
        ossMap.put(StorageProvider.S3, s3);
    }

    public void refresh(StorageProvider provider) {
        this.provider = provider;
        storage = ossMap.get(provider) != null ? ossMap.get(provider) : applicationContext.getBean(S3.class);
        init();
    }

    public void init() {
        storage.init();
    }

    public StorageResponse upload(InputStream inputStream, String fileName) {
        return storage.upload(inputStream, fileName);
    }

    public InputStream download(String fileName, Long start, Long end) {
        if (start != null && start < 0) {
            throw new BeethovenException("Start position of file can't less than 0!");
        }
        if (end != null && end < 0) {
            throw new BeethovenException("End position of file can't less than 0!");
        }
        if (start != null && end != null && end <= start) {
            throw new BeethovenException("Start must less than end!");
        }
        Long length = null;
        if (start != null && end != null) {
            length = end - start + 1;
        } else if (start == null && end != null) {
            length = end;
        }
        return storage.download(fileName, start, length);
    }

    public String getURL(String fileName) {
        return storage.getURL(fileName);
    }

    public void remove(String fileName) {
        storage.remove(fileName);
    }

    public void getAllFiles() {
        storage.getAllFiles();
    }

    public String getEndpoint() {
        return GlobalConfig.getStorageInfo().getEndpoint();
    }
}

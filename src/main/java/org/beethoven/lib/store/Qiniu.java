package org.beethoven.lib.store;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.beethoven.lib.GlobalConfig;
import org.beethoven.lib.exception.StorageException;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-11-21
 */

@Component
public class Qiniu implements Storage {

    private Auth auth;

    private UploadManager uploadManager;

    private BucketManager bucketManager;

    private org.beethoven.pojo.entity.Storage storage;

    @Override
    public void init() {
        storage = GlobalConfig.getStorage();
        auth = Auth.create(storage.getAccessKey(), storage.getSecretKey());

        Configuration config = new Configuration();
        config.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;
        uploadManager = new UploadManager(config);

        bucketManager = new BucketManager(auth, config);
    }

    @Override
    public StorageResponse upload(InputStream inputStream, String fileName) {
        String token = auth.uploadToken(storage.getBucket());
        StorageResponse response = new StorageResponse();
        try {
            Response uploadMusicResponse = uploadManager.put(inputStream, fileName, token, null, null);
            response.setOk(uploadMusicResponse.isOK());
            response.setHash((String) uploadMusicResponse.jsonToMap().get("hash"));
        } catch (QiniuException e) {
            throw new RuntimeException(e);
        }

        return response;
    }

    @Override
    public InputStream download(String fileName, Long start, Long end) {
        return null;
    }

    @Override
    public String getURL(String fileName) {
        return "";
    }

    @Override
    public void remove(String fileName) throws StorageException {

    }
}

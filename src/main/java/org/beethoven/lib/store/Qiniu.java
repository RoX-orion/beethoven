package org.beethoven.lib.store;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${oss.qiniu.access-key}")
    private String accessKey;

    @Value("${oss.qiniu.secret-key}")
    private String secretKey;

    @PostConstruct
    public void init() {
        auth = Auth.create(accessKey, secretKey);

        Configuration config = new Configuration();
        config.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;
        uploadManager = new UploadManager(config);

        bucketManager = new BucketManager(auth, config);
    }

    @Override
    public StorageResponse upload(InputStream inputStream, String bucket, String fileName) {
        String token = auth.uploadToken(bucket);
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
    public void download() {

    }
}

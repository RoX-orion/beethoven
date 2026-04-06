package org.beethoven.lib.store;

import org.beethoven.lib.GlobalConfig;
import org.beethoven.pojo.entity.StorageInfo;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.InputStream;

/**
 * Copyright (c) 2025 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2025-11-15
 */

@Component
public class S3 implements Storage {

    private boolean directLink = true;

    private StorageInfo storageInfo;

    @Override
    public void init() {
        storageInfo = GlobalConfig.getStorageInfo();
    }

    @Override
    public StorageResponse upload(InputStream inputStream, String fileName) {
        return null;
    }

    @Override
    public InputStream download(String fileName, Long start, Long length) {
        return null;
    }

    @Override
    public String getURL(String fileName) {
        if (directLink) {
            return GlobalConfig.endpoint + storageInfo.getBucket() + "/" + fileName;
        } else {
            try (S3Presigner preSigner = S3Presigner.create()) {

                GetObjectRequest objectRequest = GetObjectRequest.builder()
                        .bucket(storageInfo.getBucket())
                        .key(fileName)
                        .build();

                GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
//                        .signatureDuration(Duration.ofMinutes(10))
                        .getObjectRequest(objectRequest)
                        .build();

                PresignedGetObjectRequest presignedRequest = preSigner.presignGetObject(presignRequest);

                return presignedRequest.url().toExternalForm();
            }
        }
    }

    @Override
    public void remove(String fileName) {

    }

    @Override
    public void getAllFiles() {

    }
}

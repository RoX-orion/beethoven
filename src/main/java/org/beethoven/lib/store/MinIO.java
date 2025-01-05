package org.beethoven.lib.store;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.beethoven.lib.GlobalConfig;
import org.beethoven.lib.exception.StorageException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-11-21
 */

@Slf4j
@Component
public class MinIO implements Storage {

    private MinioClient minioClient = null;
    private org.beethoven.pojo.entity.Storage storage;

    @Override
    public void init() {
        storage = GlobalConfig.getStorage();
        minioClient = MinioClient.builder()
                        .endpoint(storage.getEndpoint())
                        .credentials(storage.getAccessKey(), storage.getSecretKey())
                        .build();
    }

    @Override
    public StorageResponse upload(InputStream inputStream, String fileName) {
        StorageResponse response = new StorageResponse();
        try {
            ObjectWriteResponse objectWriteResponse = minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(storage.getBucket())
                            .object(fileName)
                            .stream(inputStream, inputStream.available(), -1)
                            .build()
            );
            response.isOk = true;
            response.hash = objectWriteResponse.etag();
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    @Override
    public InputStream download(String fileName, Long start, Long length) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(storage.getBucket())
                            .object(fileName)
                            .offset(start)
                            .length(length)
                            .build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getURL(String fileName) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                                    .method(Method.GET)
                                    .bucket(storage.getBucket())
                                    .object(fileName)
                                    .expiry(2, TimeUnit.HOURS)
                                    .build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                XmlParserException e) {
            log.error(e.getMessage());
            throw new StorageException("Get file URL fail!");
        }
    }
}

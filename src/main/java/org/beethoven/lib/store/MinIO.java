package org.beethoven.lib.store;

//import com.alibaba.nacos.api.config.annotation.NacosValue;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.beethoven.lib.GlobalConfig;
import org.beethoven.lib.exception.StorageException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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

//    @NacosValue(value = "${minio.direct:true}", autoRefreshed = true)
    private boolean directLink = true;

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
            e.printStackTrace();
            throw new RuntimeException("Upload minio file fail!");
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
            e.printStackTrace();
            throw new RuntimeException("Download minio file fail!");
        }
    }

    @Override
    public String getURL(String fileName) {
        if (directLink) {
            return GlobalConfig.endpoint + storage.getBucket() + "/" + fileName;
        } else {
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
                throw new StorageException("Get minio file URL fail!");
            }
        }
    }

    @Override
    public void remove(String fileName) {
        if (!StringUtils.hasText(fileName))
            return;
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                            .bucket(storage.getBucket())
                            .object(fileName)
                            .build()
            );
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error(e.getMessage());
            throw new StorageException("Delete minio file fail!");
        }
    }

    @Override
    public void getAllFiles() {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(storage.getBucket())
                        .prefix("/music")
                        .recursive(true)
                        .build());
        for (Result<Item> result : results) {
            Item item;
            try {
                item = result.get();
                System.out.println("- " + item.objectName() + " (Size: " + item.size() + ")");
            } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                     InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                     XmlParserException e) {
                log.error(e.getMessage());
            }
        }
    }
}

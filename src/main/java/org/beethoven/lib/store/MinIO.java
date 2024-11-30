package org.beethoven.lib.store;

import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-11-21
 */

@Component
public class MinIO implements Storage {

    @Value("${oss.minio.access-key}")
    private String accessKey;

    @Value("${oss.minio.secret-key}")
    private String secretKey;

    @Value("${oss.minio.endpoint}")
    private String endpoint;

    private MinioClient minioClient = null;

    @PostConstruct
    public void init() {
        minioClient = MinioClient.builder()
                        .endpoint(endpoint)
                        .credentials(accessKey, secretKey)
                        .build();
    }

    @Override
    public StorageResponse upload(InputStream inputStream, String bucket, String fileName) {
        StorageResponse response = new StorageResponse();
        try {
            ObjectWriteResponse objectWriteResponse = minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object("/mm/a.png")
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
    public void download() {

    }
}

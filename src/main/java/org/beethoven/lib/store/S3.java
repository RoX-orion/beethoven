package org.beethoven.lib.store;

import org.beethoven.lib.GlobalConfig;
import org.beethoven.lib.exception.StorageException;
import org.beethoven.pojo.entity.StorageInfo;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.InputStream;
import java.net.URI;

/**
 * S3-compatible storage implementation.
 */
@Component
public class S3 implements Storage {

    private static final Region DEFAULT_REGION = Region.US_EAST_1;

    private boolean directLink = true;

    private StorageInfo storageInfo;

    private S3Client s3Client;

    @Override
    public synchronized void init() {
        storageInfo = GlobalConfig.getStorageInfo();
        if (!StringUtils.hasText(storageInfo.getAccessKey())
                || !StringUtils.hasText(storageInfo.getSecretKey())
                || !StringUtils.hasText(storageInfo.getBucket())
                || !StringUtils.hasText(storageInfo.getEndpoint())) {
            throw new StorageException("S3 storage configuration is incomplete");
        }

        if (s3Client != null) {
            s3Client.close();
        }

        S3ClientBuilder builder = S3Client.builder()
                .region(DEFAULT_REGION)
                .credentialsProvider(credentialsProvider())
                .endpointOverride(URI.create(storageInfo.getEndpoint()))
                .forcePathStyle(true);
        s3Client = builder.build();
    }

    @Override
    public StorageResponse upload(InputStream inputStream, String fileName) {
        ensureInitialized();
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(storageInfo.getBucket())
                    .key(fileName)
                    .build();
            PutObjectResponse putObjectResponse = s3Client.putObject(
                    request,
                    RequestBody.fromContentProvider(() -> inputStream, "application/octet-stream")
            );
            return successfulResponse(putObjectResponse);
        } catch (SdkException e) {
            throw new StorageException("Upload S3 file failed", e);
        }
    }

    @Override
    public StorageResponse upload(InputStream inputStream, String fileName, long contentLength) {
        if (contentLength < 0) {
            return upload(inputStream, fileName);
        }
        ensureInitialized();
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(storageInfo.getBucket())
                    .key(fileName)
                    .build();
            PutObjectResponse putObjectResponse = s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(inputStream, contentLength)
            );
            return successfulResponse(putObjectResponse);
        } catch (SdkException e) {
            throw new StorageException("Upload S3 file failed", e);
        }
    }

    @Override
    public InputStream download(String fileName, Long start, Long length) {
        ensureInitialized();
        String objectKey = objectKey(fileName);
        GetObjectRequest.Builder requestBuilder = GetObjectRequest.builder()
                .bucket(storageInfo.getBucket())
                .key(objectKey);
        if (start != null && length != null) {
            requestBuilder.range("bytes=" + start + "-" + (start + length - 1));
        } else if (start != null) {
            requestBuilder.range("bytes=" + start + "-");
        } else if (length != null) {
            requestBuilder.range("bytes=-" + length);
        }

        try {
            return s3Client.getObject(requestBuilder.build());
        } catch (SdkException e) {
            throw new StorageException("Download S3 file failed", e);
        }
    }

    @Override
    public String getURL(String fileName) {
        ensureInitialized();
        if (directLink) {
            return endpointWithSlash() + storageInfo.getBucket() + "/" + objectKey(fileName);
        }

        try (S3Presigner preSigner = presigner()) {
            GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .bucket(storageInfo.getBucket())
                    .key(fileName)
                    .build();
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .getObjectRequest(objectRequest)
                    .build();
            PresignedGetObjectRequest presignedRequest = preSigner.presignGetObject(presignRequest);
            return presignedRequest.url().toExternalForm();
        } catch (SdkException e) {
            throw new StorageException("Create S3 file URL failed", e);
        }
    }

    @Override
    public void remove(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return;
        }
        ensureInitialized();
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(storageInfo.getBucket())
                    .key(objectKey(fileName))
                    .build());
        } catch (SdkException e) {
            throw new StorageException("Delete S3 file failed", e);
        }
    }

    @Override
    public void getAllFiles() {
        // The Storage interface predates the S3 implementation and does not expose a result type.
        // Listing is intentionally not performed here.
    }

    private StorageResponse successfulResponse(PutObjectResponse putObjectResponse) {
        StorageResponse response = new StorageResponse();
        response.setOk(true);
        response.setHash(putObjectResponse.eTag());
        return response;
    }

    private StaticCredentialsProvider credentialsProvider() {
        return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(storageInfo.getAccessKey(), storageInfo.getSecretKey())
        );
    }

    private S3Presigner presigner() {
        return S3Presigner.builder()
                .region(DEFAULT_REGION)
                .credentialsProvider(credentialsProvider())
                .endpointOverride(URI.create(storageInfo.getEndpoint()))
                .build();
    }

    private String endpointWithSlash() {
        return storageInfo.getEndpoint().endsWith("/")
                ? storageInfo.getEndpoint()
                : storageInfo.getEndpoint() + "/";
    }

    private String objectKey(String fileName) {
        String prefix = endpointWithSlash() + storageInfo.getBucket() + "/";
        return fileName.startsWith(prefix) ? fileName.substring(prefix.length()) : fileName;
    }

    private void ensureInitialized() {
        if (s3Client == null || storageInfo == null) {
            throw new StorageException("S3 storage is not initialized");
        }
    }
}

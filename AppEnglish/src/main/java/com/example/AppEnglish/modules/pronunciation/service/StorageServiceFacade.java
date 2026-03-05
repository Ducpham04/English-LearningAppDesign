package com.example.AppEnglish.modules.pronunciation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class StorageServiceFacade {

    private final S3Client s3Client;

    public void uploadInputStream(String bucket, String key, InputStream input, long size, String contentType) {
        PutObjectRequest.Builder b = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key);
        if (contentType != null && !contentType.isBlank()) {
            b.contentType(contentType);
        }

        s3Client.putObject(b.build(), RequestBody.fromInputStream(input, size));
    }

    public void uploadBytes(String bucket, String key, byte[] bytes, String contentType) {
        PutObjectRequest.Builder b = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key);
        if (contentType != null && !contentType.isBlank()) {
            b.contentType(contentType);
        }
        s3Client.putObject(b.build(), RequestBody.fromBytes(bytes));
    }

    public long headContentLength(String bucket, String key) {
        HeadObjectResponse r = s3Client.headObject(HeadObjectRequest.builder().bucket(bucket).key(key).build());
        return r.contentLength();
    }

    public ResponseInputStream<GetObjectResponse> openStream(String bucket, String key) {
        return s3Client.getObject(GetObjectRequest.builder().bucket(bucket).key(key).build());
    }

    public void copyObject(String bucket, String fromKey, String toKey) {
        String copySource = bucket + "/" + fromKey;
        s3Client.copyObject(CopyObjectRequest.builder()
                .destinationBucket(bucket)
                .destinationKey(toKey)
                .copySource(copySource)
                .build());
    }

    public void deleteObject(String bucket, String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
    }
}

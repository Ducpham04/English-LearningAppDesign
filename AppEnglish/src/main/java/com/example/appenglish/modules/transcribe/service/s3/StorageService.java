package com.example.appenglish.modules.transcribe.service.s3;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StorageService {

    @NonFinal
    @Value("${aws.s3.bucket}")
    String bucket;

    @NonFinal
    @Value("${aws.region}")
    String region;

    S3Client s3Client;

    public String uploadFile(Path file, String key) {
        s3Client.putObject(
                PutObjectRequest.builder().bucket(bucket).key(key).build(),
                file);
        return buildPublicUrl(key);
    }

    public String uploadMultipartFile(MultipartFile file, String key) throws IOException {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType(file.getContentType())
                        .build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );
        return buildPublicUrl(key);
    }

    private String buildPublicUrl(String key) {
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
    }
}

package com.example.appenglish.config;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.transcribe.TranscribeClient;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.utils.StringUtils;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AwsConfig {

    @Value("${aws.accessKey}")
    String accessKey;

    @Value("${aws.secretKey}")
    String secretKey;

    @Value("${aws.region}")
    String region;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(awsCredentialsProvider())
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(awsCredentialsProvider())
                .build();
    }

    @Bean
    public TranscribeClient transcribeClient() {
        return TranscribeClient.builder()
                .region(Region.of(region))
                .credentialsProvider(awsCredentialsProvider())
                .build();
    }

    @Bean
    public TranslateClient translateClient() {
        return TranslateClient.builder()
                .region(Region.of(region))
                .credentialsProvider(awsCredentialsProvider())
                .build();
    }

    private AwsCredentialsProvider awsCredentialsProvider() {
        // If keys are not provided (common in local dev), fall back to the default chain
        // (env, system properties, profile, etc.). This allows the app to start; AWS calls
        // will still fail later if no credentials are available.
        if (StringUtils.isBlank(accessKey) || StringUtils.isBlank(secretKey)) {
            return DefaultCredentialsProvider.create();
        }
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey.trim(), secretKey.trim());
        return StaticCredentialsProvider.create(credentials);
    }
}

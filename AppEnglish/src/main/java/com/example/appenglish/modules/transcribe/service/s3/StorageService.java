package com.example.appenglish.modules.transcribe.service.s3;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.Duration;

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
    S3Presigner s3Presigner;

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

    /**
     * Trả về URL phát video: nếu storedUrl là S3 (https://bucket.s3.../key, s3://bucket/key, hoặc chỉ key)
     * thì trả về presigned URL (1 giờ), ngược lại trả về storedUrl.
     */
    public String getVideoPlaybackUrl(String storedUrl) {
        if (storedUrl == null || storedUrl.isBlank()) return storedUrl;
        String key = null;
        if (storedUrl.startsWith("s3://")) {
            int bucketEnd = storedUrl.indexOf("/", 5);
            if (bucketEnd > 0 && bucketEnd < storedUrl.length() - 1) {
                key = storedUrl.substring(bucketEnd + 1);
            }
        } else if (storedUrl.startsWith("https://") || storedUrl.startsWith("http://")) {
            String prefix = "https://" + bucket + ".s3." + region + ".amazonaws.com/";
            if (storedUrl.startsWith(prefix)) {
                key = storedUrl.substring(prefix.length());
            }
        } else {
            // Lưu trong DB chỉ là key (vd: user-uploads/102/videoplayback.mp4)
            String trimmed = storedUrl.trim();
            if (!trimmed.isEmpty() && !trimmed.contains("://")) {
                key = trimmed.startsWith("/") ? trimmed.substring(1) : trimmed;
            }
        }
        if (key == null || key.isBlank()) return storedUrl;
        try {
            GetObjectRequest getReq = GetObjectRequest.builder().bucket(bucket).key(key).build();
            GetObjectPresignRequest presignReq = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofHours(1))
                    .getObjectRequest(getReq)
                    .build();
            PresignedGetObjectRequest presigned = s3Presigner.presignGetObject(presignReq);
            return presigned.url().toString();
        } catch (Exception e) {
            // Presign có thể lỗi (credentials, region...). Trả về public URL để FE vẫn thử load
            // (bucket public thì phát được; bucket private sẽ 403 và FE báo lỗi).
            return buildPublicUrl(key);
        }
    }

    /**
     * Trích key S3 từ storedUrl (s3://..., https://... hoặc key thuần). Trả về null nếu không nhận dạng được.
     */
    public String getVideoKey(String storedUrl) {
        if (storedUrl == null || storedUrl.isBlank()) return null;
        if (storedUrl.startsWith("s3://")) {
            int bucketEnd = storedUrl.indexOf("/", 5);
            if (bucketEnd > 0 && bucketEnd < storedUrl.length() - 1)
                return storedUrl.substring(bucketEnd + 1);
            return null;
        }
        if (storedUrl.startsWith("https://") || storedUrl.startsWith("http://")) {
            String prefix = "https://" + bucket + ".s3." + region + ".amazonaws.com/";
            if (storedUrl.startsWith(prefix))
                return storedUrl.substring(prefix.length());
            return null;
        }
        String trimmed = storedUrl.trim();
        if (trimmed.isEmpty() || trimmed.contains("://")) return null;
        return trimmed.startsWith("/") ? trimmed.substring(1) : trimmed;
    }

    /**
     * Mở stream video từ S3 (hỗ trợ Range để seek). Caller phải đóng stream sau khi dùng xong.
     */
    public VideoStreamResult openVideoStream(String key, String rangeHeader) throws IOException {
        GetObjectRequest.Builder reqBuilder = GetObjectRequest.builder().bucket(bucket).key(key);
        if (rangeHeader != null && !rangeHeader.isBlank())
            reqBuilder.range(rangeHeader);
        ResponseInputStream<GetObjectResponse> stream = s3Client.getObject(reqBuilder.build());
        GetObjectResponse res = stream.response();
        long contentLength = res.contentLength() != null ? res.contentLength() : 0;
        String contentType = res.contentType() != null ? res.contentType() : "video/mp4";
        String contentRange = res.contentRange();
        return new VideoStreamResult(stream, contentLength, contentType, contentRange);
    }

    /** Kết quả stream video từ S3; phải gọi close() khi dùng xong. */
    @lombok.Getter
    @lombok.RequiredArgsConstructor
    public static class VideoStreamResult implements AutoCloseable {
        private final ResponseInputStream<GetObjectResponse> stream;
        private final long contentLength;
        private final String contentType;
        private final String contentRange;

        public InputStream getInputStream() {
            return stream;
        }

        public boolean isPartial() {
            return contentRange != null && !contentRange.isBlank();
        }

        @Override
        public void close() {
            if (stream != null) {
                try { stream.close(); } catch (IOException ignored) {}
            }
        }
    }
}

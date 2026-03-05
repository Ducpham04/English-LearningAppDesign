package com.example.AppEnglish.modules.pronunciation.service.kafka.consumer;

import com.example.AppEnglish.entity.PronunciationAttempt;
import com.example.AppEnglish.enums.S3UploadStatus;
import com.example.AppEnglish.modules.pronunciation.dto.event.PronunciationAudioFinalizeEvent;
import com.example.AppEnglish.modules.pronunciation.service.StorageServiceFacade;
import com.example.AppEnglish.repository.PronunciationAttemptRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PronunciationAudioFinalizeConsumer {

    PronunciationAttemptRepository attemptRepository;
    StorageServiceFacade storageServiceFacade;

    @Value("${aws.s3.bucket}")
    @NonFinal
    String bucket;

    @KafkaListener(
            topics = "${spring.kafka.topics.pronunciation-finalize:pronunciation-finalize}",
            groupId = "pronunciation-finalize-group"
    )
    public void consume(@Payload PronunciationAudioFinalizeEvent event) {
        PronunciationAttempt attempt = attemptRepository.findById(event.getAttemptId())
                .orElseThrow(() -> new IllegalArgumentException("PronunciationAttempt not found: " + event.getAttemptId()));

        if (attempt.getS3Status() == S3UploadStatus.UPLOADED && attempt.getS3Key() != null && !attempt.getS3Key().isBlank()) {
            return;
        }

        try {
            storageServiceFacade.copyObject(bucket, event.getStagingKey(), event.getFinalKey());
            // optional: delete staging
            storageServiceFacade.deleteObject(bucket, event.getStagingKey());

            attempt.setS3Key("s3://" + bucket + "/" + event.getFinalKey());
            attempt.setS3Status(S3UploadStatus.UPLOADED);
            attemptRepository.save(attempt);

        } catch (Exception e) {
            log.error("Finalize pronunciation audio failed attemptId={}", attempt.getId(), e);
            attempt.setS3Status(S3UploadStatus.FAILED);
            attemptRepository.save(attempt);
        }
    }
}


package com.example.AppEnglish.modules.pronunciation.service.kafka.consumer;

import com.example.AppEnglish.entity.PronunciationAttempt;
import com.example.AppEnglish.enums.S3UploadStatus;
import com.example.AppEnglish.modules.pronunciation.dto.event.PronunciationAudioUploadEvent;
import com.example.AppEnglish.modules.pronunciation.dto.event.PronunciationAudioFinalizeEvent;
import com.example.AppEnglish.repository.PronunciationAttemptRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PronunciationAudioUploadConsumer {

    PronunciationAttemptRepository attemptRepository;
    KafkaTemplate<String, PronunciationAudioFinalizeEvent> kafkaTemplate;

    @Value("${spring.kafka.topics.pronunciation-finalize:pronunciation-finalize}")
    @NonFinal
    String finalizeTopic;

    @KafkaListener(
            topics = "${spring.kafka.topics.pronunciation-upload:pronunciation-upload}",
            groupId = "pronunciation-upload-group"
    )
    public void consume(@Payload PronunciationAudioUploadEvent event) {
        PronunciationAttempt attempt = attemptRepository.findById(event.getAttemptId())
                .orElseThrow(() -> new IllegalArgumentException("PronunciationAttempt not found: " + event.getAttemptId()));

        if (attempt.getS3Status() == S3UploadStatus.UPLOADED && attempt.getS3Key() != null && !attempt.getS3Key().isBlank()) {
            return;
        }

        String finalKey = buildFinalKey(attempt, event.getContentType());

        PronunciationAudioFinalizeEvent finalizeEvent = PronunciationAudioFinalizeEvent.builder()
                .attemptId(attempt.getId())
                .stagingKey(event.getStagingKey())
                .finalKey(finalKey)
                .contentType(event.getContentType())
                .build();

        kafkaTemplate.send(finalizeTopic, String.valueOf(attempt.getId()), finalizeEvent);
    }

    private String buildFinalKey(PronunciationAttempt attempt, String contentType) {
        String ext = ".dat";
        if (contentType != null) {
            String ct = contentType.toLowerCase();
            if (ct.contains("wav")) ext = ".wav";
            else if (ct.contains("webm")) ext = ".webm";
            else if (ct.contains("ogg")) ext = ".ogg";
            else if (ct.contains("mpeg")) ext = ".mp3";
            else if (ct.contains("mp4") || ct.contains("m4a") || ct.contains("aac")) ext = ".m4a";
        }
        return "pronunciation/user-" + attempt.getUser().getId()
                + "/lesson-" + attempt.getLesson().getId()
                + "/attempt-" + attempt.getId()
                + ext;
    }
}

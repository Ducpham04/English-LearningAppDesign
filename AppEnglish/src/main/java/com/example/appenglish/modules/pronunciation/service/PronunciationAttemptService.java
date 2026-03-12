package com.example.appenglish.modules.pronunciation.service;

import com.example.appenglish.entity.LessonVideo;
import com.example.appenglish.entity.PronunciationAttempt;
import com.example.appenglish.entity.User;
import com.example.appenglish.enums.PronunciationAttemptStatus;
import com.example.appenglish.enums.S3UploadStatus;
import com.example.appenglish.modules.pronunciation.dto.AzureAssessmentResult;
import com.example.appenglish.modules.pronunciation.dto.PronunciationScoreResponse;
import com.example.appenglish.modules.pronunciation.dto.event.PronunciationAudioUploadEvent;
import com.example.appenglish.repository.LessonVideoRepository;
import com.example.appenglish.repository.PronunciationAttemptRepository;
import com.microsoft.cognitiveservices.speech.ResultReason;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PronunciationAttemptService {

    PronunciationAttemptRepository attemptRepository;
    LessonVideoRepository lessonVideoRepository;
    AzureStreamingPronunciationService azureService;
    TranscriptSentenceResolver transcriptSentenceResolver;
    KafkaTemplate<String, PronunciationAudioUploadEvent> kafkaTemplate;

    StorageServiceFacade storageServiceFacade;

    @Value("${spring.kafka.topics.pronunciation-upload:pronunciation-upload}")
    @NonFinal
    String uploadTopic;

    @Value("${aws.s3.bucket}")
    @NonFinal
    String bucket;

    @Value("${app.pronunciation.max-audio-size-bytes:10485760}")
    @NonFinal
    long maxAudioSizeBytes;

    @Transactional
    public PronunciationScoreResponse scoreAndEnqueueUpload(User user,
                                                           Long lessonId,
                                                           Integer sentenceIndex,
                                                           String expectedTextOverride,
                                                           MultipartFile audioFile) throws Exception {

        // Vẫn cần LessonVideo để liên kết attempt -> lesson và lưu transcript context.
        // Với bài Pronunciation (từ/cụm từ), FE có thể gửi expectedTextOverride để chấm trực tiếp.
        LessonVideo lessonVideo = lessonVideoRepository.findByLessonIdForUpdate(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("LessonVideo not found for lessonId: " + lessonId));

        String expectedText;
        if (expectedTextOverride != null && !expectedTextOverride.trim().isBlank()) {
            expectedText = expectedTextOverride.trim();
        } else {
            expectedText = transcriptSentenceResolver.resolveExpectedText(lessonVideo, sentenceIndex);
        }

        PronunciationAttempt attempt = new PronunciationAttempt();
        attempt.setUser(user);
        attempt.setLesson(lessonVideo.getLesson());
        attempt.setExpectedText(expectedText);
        attempt.setStatus(PronunciationAttemptStatus.PROCESSING);
        attempt.setS3Status(S3UploadStatus.PENDING);
        attempt.setAudioContentType(audioFile.getContentType());
        attempt = attemptRepository.save(attempt);

        String contentType = audioFile.getContentType();
        String ext = extFromContentTypeOrName(contentType, audioFile.getOriginalFilename());
        String stagingKey = "pronunciation-staging/user-" + user.getId()
                + "/lesson-" + lessonId
                + "/attempt-" + attempt.getId()
                + "/" + Instant.now().toEpochMilli() + ext;

        long multipartSize = audioFile.getSize();

        // Read request audio into RAM (bounded)
        byte[] requestBytes;
        try (InputStream reqIn = audioFile.getInputStream()) {
            requestBytes = BoundedInputStreamReader.readAllBytes(reqIn, maxAudioSizeBytes);
        } catch (Exception e) {
            attempt.setStatus(PronunciationAttemptStatus.FAILED);
            attempt.setS3Status(S3UploadStatus.FAILED);
            attempt.setErrorMessage("Read upload audio failed: " + e.getMessage());
            attemptRepository.save(attempt);
            throw e;
        }

        // Upload staging using bytes to guarantee full content
        try {
            storageServiceFacade.uploadBytes(bucket, stagingKey, requestBytes, contentType);
            long s3Len = storageServiceFacade.headContentLength(bucket, stagingKey);
            log.info("Pronunciation staging uploaded: attemptId={} multipartSize={} requestBytes={} s3Len={} contentType={} stagingKey={}",
                    attempt.getId(), multipartSize, requestBytes.length, s3Len, contentType, stagingKey);

            if (s3Len != requestBytes.length) {
                throw new IllegalStateException("Staging object size mismatch: localBytes=" + requestBytes.length + " s3Len=" + s3Len);
            }
        } catch (Exception e) {
            attempt.setS3Status(S3UploadStatus.FAILED);
            attempt.setStatus(PronunciationAttemptStatus.FAILED);
            attempt.setErrorMessage("S3 staging upload failed: " + e.getMessage());
            attemptRepository.save(attempt);
            throw e;
        }

        // Score with Azure using bytes-aware service (supports ffmpeg file fallback)
        AzureAssessmentResult azure;
        try {
            azure = azureService.assess(requestBytes, expectedText, ext);
        } catch (Exception e) {
            attempt.setStatus(PronunciationAttemptStatus.FAILED);
            attempt.setErrorMessage(e.getMessage());
            attemptRepository.save(attempt);
            throw e;
        }

        attempt.setRecognizedText(azure.getRecognizedText());
        attempt.setPronunciationScore(azure.getPronunciationScore());
        attempt.setAccuracyScore(azure.getAccuracyScore());
        attempt.setFluencyScore(azure.getFluencyScore());
        attempt.setProsodyScore(azure.getProsodyScore());
        attempt.setCompletenessScore(azure.getCompletenessScore());
        attempt.setFeedback(azure.getRawFeedback());

        boolean success = azure.getReason() == ResultReason.RecognizedSpeech
                && (azure.getPronunciationScore() != null
                || azure.getAccuracyScore() != null
                || azure.getFluencyScore() != null
                || azure.getProsodyScore() != null);

        if (success) {
            attempt.setStatus(PronunciationAttemptStatus.SCORED);
            attemptRepository.save(attempt);

            PronunciationAudioUploadEvent event = PronunciationAudioUploadEvent.builder()
                    .attemptId(attempt.getId())
                    .stagingKey(stagingKey)
                    .contentType(contentType)
                    .build();
            kafkaTemplate.send(uploadTopic, String.valueOf(attempt.getId()), event);

            return PronunciationScoreResponse.builder()
                    .attemptId(attempt.getId())
                    .lessonId(lessonId)
                    .sentenceIndex(sentenceIndex)
                    .expectedText(expectedText)
                    .recognizedText(attempt.getRecognizedText())
                    .pronunciationScore(attempt.getPronunciationScore())
                    .accuracyScore(attempt.getAccuracyScore())
                    .fluencyScore(attempt.getFluencyScore())
                    .prosodyScore(attempt.getProsodyScore())
                    .completenessScore(attempt.getCompletenessScore())
                    .status(attempt.getStatus())
                    .s3Status(attempt.getS3Status())
                    .feedback(attempt.getFeedback())
                    .build();
        }

        attempt.setStatus(PronunciationAttemptStatus.FAILED);
        String msg = "Azure did not recognize speech (reason=" + azure.getReason() + ")";
        if (azure.getCancellationDetails() != null && !azure.getCancellationDetails().isBlank()) {
            msg += ": " + azure.getCancellationDetails();
        }
        attempt.setErrorMessage(msg);
        attemptRepository.save(attempt);

        throw new RuntimeException(msg);
    }

    private String extFromContentTypeOrName(String contentType, String originalFilename) {
        if (originalFilename != null) {
            int dot = originalFilename.lastIndexOf('.');
            if (dot > -1) {
                return originalFilename.substring(dot);
            }
        }
        if (contentType == null) return ".dat";
        String ct = contentType.toLowerCase();
        if (ct.contains("wav")) return ".wav";
        if (ct.contains("webm")) return ".webm";
        if (ct.contains("ogg")) return ".ogg";
        if (ct.contains("mpeg")) return ".mp3";
        if (ct.contains("mp4") || ct.contains("m4a") || ct.contains("aac")) return ".m4a";
        return ".dat";
    }
}

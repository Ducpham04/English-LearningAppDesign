package com.example.appenglish.modules.transcribe.service.kafka.consumer;

import com.example.appenglish.modules.transcribe.dto.TranscriptSentence;
import com.example.appenglish.modules.transcribe.dto.event.UploadTranscribeEvent;
import com.example.appenglish.modules.transcribe.service.s3.StorageService;
import com.example.appenglish.modules.transcribe.service.transcript.AwsTranscribeTranscriptParser;
import com.example.appenglish.modules.transcribe.service.transcript.LessonVideoTranscriptPersistenceService;
import com.example.appenglish.modules.transcribe.service.transcript.TranscriptionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UploadTranscribeConsumer {

    TranscriptionService transcriptionService;
    AwsTranscribeTranscriptParser transcriptParser;
    LessonVideoTranscriptPersistenceService persistenceService;
    StorageService storageService;

    @KafkaListener(
            topics = "${spring.kafka.topics.upload-transcribe}",
            groupId = "upload-transcribe-group"
    )
    public void consume(@Payload UploadTranscribeEvent event) {
        log.info("Processing upload transcription request: {}", event.getTransactionId());

        Long lessonId = event.getLessonId();
        String s3Uri = event.getS3Uri();

        try {
            persistenceService.markProcessing(lessonId, s3Uri, s3Uri);

            String jobName = "upload-transcribe-" + event.getTransactionId();
            String languageCode = event.getLanguageCode() == null || event.getLanguageCode().isBlank()
                    ? "en-US"
                    : event.getLanguageCode();

            transcriptionService.startJob(jobName, s3Uri, languageCode);

            String transcriptJson = transcriptionService.fetchTranscript(jobName);
            List<TranscriptSentence> sentences = transcriptParser.parseSentences(transcriptJson);

            persistenceService.saveTranscriptForLessonVideo(lessonId, s3Uri, s3Uri, sentences, transcriptJson);

            log.info("Upload transcription completed: lessonId={}", lessonId);
        } catch (Exception e) {
            log.error("Error processing upload transcription: transactionId={}", event.getTransactionId(), e);
            persistenceService.markFailed(lessonId, s3Uri, s3Uri, e.getMessage());
        }
    }
}


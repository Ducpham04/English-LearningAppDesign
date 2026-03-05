package com.example.AppEnglish.modules.transcribe.service.kafka.consumer;

import com.example.AppEnglish.modules.transcribe.dto.TranscriptSentence;
import com.example.AppEnglish.modules.transcribe.dto.event.UploadTranscribeEvent;
import com.example.AppEnglish.modules.transcribe.service.s3.StorageService;
import com.example.AppEnglish.modules.transcribe.service.transcript.AwsTranscribeTranscriptParser;
import com.example.AppEnglish.modules.transcribe.service.transcript.LessonVideoTranscriptPersistenceService;
import com.example.AppEnglish.modules.transcribe.service.transcript.TranscriptionService;
import com.example.AppEnglish.modules.transcribe.service.transcript.EspeakIpaService;
import com.example.AppEnglish.modules.transcribe.service.translate.AwsTranslateService;
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
    AwsTranslateService awsTranslateService;
    EspeakIpaService espeakIpaService;

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

            String sourceLang = "en";
            if (languageCode != null) {
                sourceLang = languageCode.contains("-") ? languageCode.substring(0, languageCode.indexOf('-')) : languageCode;
            }
            log.info("Starting translation for lessonId={} from {} to vi. Sentences count: {}", lessonId, sourceLang, sentences.size());

            int successCount = 0;
            for (TranscriptSentence s : sentences) {
                try {
                    String translated = awsTranslateService.translateToVietnamese(s.getText(), sourceLang);
                    s.setViText(translated);
                    if (translated != null && !translated.isBlank()) {
                        successCount++;
                    }
                } catch (Exception ex) {
                    log.warn("Translate failed for lessonId={} text={} error={}", lessonId, s.getText(), ex.getMessage());
                }
            }
            log.info("Translation completed for lessonId={}. Success: {}/{}", lessonId, successCount, sentences.size());

            log.info("Starting IPA generation for lessonId={}. Sentences count: {}", lessonId, sentences.size());
            int ipaSuccess = 0;
            for (TranscriptSentence s : sentences) {
                try {
                    String ipa = espeakIpaService.toIpa(s.getText());
                    s.setIpa(ipa);
                    if (ipa != null && !ipa.isBlank()) {
                        ipaSuccess++;
                    }
                } catch (Exception ex) {
                    log.warn("IPA generation failed for lessonId={} text={} error={}", lessonId, s.getText(), ex.getMessage());
                }
            }
            log.info("IPA generation completed for lessonId={}. Success: {}/{}", lessonId, ipaSuccess, sentences.size());

            persistenceService.saveTranscriptForLessonVideo(lessonId, s3Uri, s3Uri, sentences, transcriptJson);

            log.info("Upload transcription completed: lessonId={}", lessonId);
        } catch (Exception e) {
            log.error("Error processing upload transcription: transactionId={}", event.getTransactionId(), e);
            persistenceService.markFailed(lessonId, s3Uri, s3Uri, e.getMessage());
        }
    }
}


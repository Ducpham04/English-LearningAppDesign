package com.example.appenglish.modules.transcribe.service.kafka.consumer;

import com.example.appenglish.modules.transcribe.dto.TranscriptSentence;
import com.example.appenglish.modules.transcribe.dto.event.YoutubeTranscribeEvent;
import com.example.appenglish.modules.transcribe.service.s3.StorageService;
import com.example.appenglish.modules.transcribe.service.transcript.AwsTranscribeTranscriptParser;
import com.example.appenglish.modules.transcribe.service.transcript.LessonVideoTranscriptPersistenceService;
import com.example.appenglish.modules.transcribe.service.transcript.TranscriptionService;
import com.example.appenglish.modules.transcribe.service.transcript.EspeakIpaService;
import com.example.appenglish.modules.transcribe.service.translate.AwsTranslateService;
import com.example.appenglish.modules.transcribe.service.youtube.YoutubeDownloaderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class YoutubeTranscribeConsumer {

    YoutubeDownloaderService youtubeDownloaderService;
    StorageService storageService;
    TranscriptionService transcriptionService;
    AwsTranscribeTranscriptParser transcriptParser;
    LessonVideoTranscriptPersistenceService persistenceService;
    AwsTranslateService awsTranslateService;
    EspeakIpaService espeakIpaService;

    @KafkaListener(
            topics = "${spring.kafka.topics.youtube-transcribe}",
            groupId = "youtube-transcribe-group"
    )
    public void consume(@Payload YoutubeTranscribeEvent event) {
        log.info("Processing YouTube transcription request: {}", event.getTransactionId());

        File downloadedFile = null;
        String s3Uri = null;

        try {
            downloadedFile = youtubeDownloaderService.downloadAudio(event.getYoutubeUrl());

            String fileKey = "youtube-audio/" + event.getTransactionId() + ".mp3";
            s3Uri = storageService.uploadFile(downloadedFile.toPath(), fileKey);

            persistenceService.markProcessing(event.getLessonId(), event.getYoutubeUrl(), s3Uri);

            String jobName = "lessonvideo-youtube-transcribe-" + event.getTransactionId();
            transcriptionService.startJob(jobName, s3Uri, "en-US");

            String transcriptJson = transcriptionService.fetchTranscript(jobName);
            List<TranscriptSentence> sentences = transcriptParser.parseSentences(transcriptJson);

            log.info("Starting translation for YouTube lessonId={} from en to vi. Sentences count: {}", event.getLessonId(), sentences.size());
            int successCount = 0;
            for (TranscriptSentence s : sentences) {
                try {
                    String translated = awsTranslateService.translateToVietnamese(s.getText(), "en");
                    s.setViText(translated);
                    if (translated != null && !translated.isBlank()) {
                        successCount++;
                    }
                } catch (Exception ex) {
                    log.warn("Translate failed for lessonId={} text={} error={}", event.getLessonId(), s.getText(), ex.getMessage());
                }
            }
            log.info("Translation completed for YouTube lessonId={}. Success: {}/{}", event.getLessonId(), successCount, sentences.size());

            log.info("Starting IPA generation for YouTube lessonId={}. Sentences count: {}", event.getLessonId(), sentences.size());
            int ipaSuccess = 0;
            for (TranscriptSentence s : sentences) {
                try {
                    String ipa = espeakIpaService.toIpa(s.getText());
                    s.setIpa(ipa);
                    if (ipa != null && !ipa.isBlank()) {
                        ipaSuccess++;
                    }
                } catch (Exception ex) {
                    log.warn("IPA generation failed for lessonId={} text={} error={}", event.getLessonId(), s.getText(), ex.getMessage());
                }
            }
            log.info("IPA generation completed for YouTube lessonId={}. Success: {}/{}", event.getLessonId(), ipaSuccess, sentences.size());

            persistenceService.saveTranscriptForLessonVideo(event.getLessonId(), event.getYoutubeUrl(), s3Uri, sentences, transcriptJson);

            log.info("Transcription completed for lessonId={} url={}", event.getLessonId(), event.getYoutubeUrl());

        } catch (Exception e) {
            log.error("Error processing YouTube transcription: {}", event.getTransactionId(), e);
            persistenceService.markFailed(event.getLessonId(), event.getYoutubeUrl(), s3Uri, e.getMessage());
        } finally {
            if (downloadedFile != null) {
                youtubeDownloaderService.cleanup(downloadedFile);
            }
        }
    }
}


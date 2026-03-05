package com.example.AppEnglish.modules.transcribe.service.kafka.producer;

import com.example.AppEnglish.modules.transcribe.dto.event.UploadTranscribeEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UploadTranscribeProducer {

    @Value("${spring.kafka.topics.upload-transcribe}")
    @NonFinal
    String uploadTranscribeTopic;
    KafkaTemplate<String, UploadTranscribeEvent> kafkaTemplate;

    public UUID sendTranscriptionRequest(Long lessonId, String s3Uri, String languageCode) {
        UUID transactionId = UUID.randomUUID();

        UploadTranscribeEvent event = UploadTranscribeEvent.builder()
                .transactionId(transactionId)
                .lessonId(lessonId)
                .s3Uri(s3Uri)
                .languageCode(languageCode)
                .build();

        Message<UploadTranscribeEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, uploadTranscribeTopic)
                .setHeader(KafkaHeaders.KEY, transactionId.toString())
                .build();

        kafkaTemplate.send(message);
        log.info("Sent upload transcription request: {}", transactionId);

        return transactionId;
    }
}


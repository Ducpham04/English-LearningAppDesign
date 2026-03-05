package com.example.AppEnglish.modules.transcribe.service.kafka.producer;

import com.example.AppEnglish.modules.transcribe.dto.event.YoutubeTranscribeEvent;
import com.example.AppEnglish.modules.transcribe.dto.request.YoutubeTranscribeRequest;
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
public class YoutubeTranscribeProducer {

    @Value("${spring.kafka.topics.youtube-transcribe}")
    @NonFinal
    String youtubeTranscribeTopic;
    KafkaTemplate<String, YoutubeTranscribeEvent> kafkaTemplate;

    public String sendTranscriptionRequest(YoutubeTranscribeRequest request) {
        String transactionId = UUID.randomUUID().toString();

        YoutubeTranscribeEvent event = YoutubeTranscribeEvent.builder()
                .transactionId(UUID.fromString(transactionId))
                .lessonId(request.getLessonId())
                .youtubeUrl(request.getYoutubeUrl())
                .build();

        Message<YoutubeTranscribeEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, youtubeTranscribeTopic)
                .setHeader(KafkaHeaders.KEY, transactionId)
                .build();

        kafkaTemplate.send(message);
        log.info("Sent YouTube transcription request: {}", transactionId);

        return transactionId;
    }
}


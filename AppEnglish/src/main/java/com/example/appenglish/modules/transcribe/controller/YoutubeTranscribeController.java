package com.example.appenglish.modules.transcribe.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.entity.LessonVideo;
import com.example.appenglish.modules.transcribe.dto.request.YoutubeTranscribeRequest;
import com.example.appenglish.modules.transcribe.service.kafka.producer.YoutubeTranscribeProducer;
import com.example.appenglish.repository.LessonVideoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/youtube")
@RequiredArgsConstructor
@Tag(name = "YouTube Transcription", description = "APIs for YouTube video transcription")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class YoutubeTranscribeController {

    YoutubeTranscribeProducer youtubeTranscribeProducer;
    LessonVideoRepository lessonVideoRepository;

    @PostMapping("/transcribe")
    @Operation(summary = "Submit YouTube video for transcription")
    public ResponseEntity<BaseResponse<String>> transcribe(@Valid @RequestBody YoutubeTranscribeRequest req,
                                                          HttpServletRequest request) {
        String transactionId = youtubeTranscribeProducer.sendTranscriptionRequest(req);
        return ResponseEntity.accepted()
                .body(BaseResponse.ok(transactionId, "Transcribe successfully", request.getRequestURI()));
    }

    @GetMapping("/{lessonId}/transcript")
    @Operation(summary = "Get transcript JSON by lessonId")
    public ResponseEntity<BaseResponse<Object>> getTranscriptByLessonId(@PathVariable Long lessonId,
                                                                        HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        lessonVideoRepository.findById(lessonId).ifPresentOrElse(lv -> {
            response.put("transcript", lv.getTranscriptJson());
            response.put("s3Url", lv.getVideoUrl());
        }, () -> {
            response.put("transcript", null);
            response.put("s3Url", null);
        });

        return ResponseEntity.ok(BaseResponse.ok(response, "OK", request.getRequestURI()));
    }
}

package com.example.appenglish.modules.transcribe.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.common.exception.ApiException;
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
        LessonVideo lv = lessonVideoRepository.findById(lessonId)
                .orElseThrow(() -> new ApiException("Lesson video not found", "LESSON_VIDEO_NOT_FOUND"));

        return ResponseEntity.ok(BaseResponse.ok(lv.getTranscriptJson(), "OK", request.getRequestURI()));
    }
}

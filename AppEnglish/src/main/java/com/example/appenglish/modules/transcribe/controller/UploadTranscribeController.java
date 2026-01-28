package com.example.appenglish.modules.transcribe.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.modules.transcribe.service.kafka.producer.UploadTranscribeProducer;
import com.example.appenglish.modules.transcribe.service.s3.StorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UploadTranscribeController {

    StorageService storageService;
    UploadTranscribeProducer uploadTranscribeProducer;

    @PostMapping(
            value = "/transcribe",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<BaseResponse<String>> uploadAndTranscribe(
            @RequestPart("file") MultipartFile file,
            @RequestParam("lessonId") Long lessonId,
            @RequestParam(value = "languageCode", defaultValue = "en-US") String languageCode,
            HttpServletRequest request
    ) throws IOException {

        String key = "user-uploads/" + lessonId + "/" + file.getOriginalFilename();
        String s3Uri = storageService.uploadMultipartFile(file, key);

        UUID transactionId = uploadTranscribeProducer.sendTranscriptionRequest(lessonId, s3Uri, languageCode);

        return ResponseEntity.accepted()
                .body(BaseResponse.ok(transactionId.toString(), "Transcribe successfully", request.getRequestURI()));
    }
}


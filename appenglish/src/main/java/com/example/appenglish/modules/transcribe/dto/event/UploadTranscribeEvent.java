package com.example.appenglish.modules.transcribe.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadTranscribeEvent {
    private UUID transactionId;
    private Long lessonId;
    private String s3Uri;
    private String languageCode;
}


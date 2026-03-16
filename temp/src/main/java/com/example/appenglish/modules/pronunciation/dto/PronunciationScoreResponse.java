package com.example.appenglish.modules.pronunciation.dto;

import com.example.appenglish.enums.PronunciationAttemptStatus;
import com.example.appenglish.enums.S3UploadStatus;
import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class PronunciationScoreResponse {
    Long attemptId;
    Long lessonId;
    Integer sentenceIndex;
    String expectedText;
    String recognizedText;

    Double pronunciationScore;
    Double accuracyScore;
    Double fluencyScore;
    Double prosodyScore;
    Double completenessScore;

    PronunciationAttemptStatus status;
    S3UploadStatus s3Status;

    Map<String, Object> feedback;

    /** Có giá trị khi status = FAILED (ví dụ: Azure timeout, không nhận diện được giọng nói). */
    String errorMessage;
}

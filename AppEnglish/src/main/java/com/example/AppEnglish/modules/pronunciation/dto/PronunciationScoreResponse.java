package com.example.AppEnglish.modules.pronunciation.dto;

import com.example.AppEnglish.enums.PronunciationAttemptStatus;
import com.example.AppEnglish.enums.S3UploadStatus;
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
}

package com.example.AppEnglish.modules.pronunciation.dto;

import com.microsoft.cognitiveservices.speech.ResultReason;
import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class AzureAssessmentResult {
    ResultReason reason;
    String recognizedText;

    Double pronunciationScore;
    Double accuracyScore;
    Double fluencyScore;
    Double prosodyScore;
    Double completenessScore;

    Map<String, Object> rawFeedback;

    String cancellationDetails;
}


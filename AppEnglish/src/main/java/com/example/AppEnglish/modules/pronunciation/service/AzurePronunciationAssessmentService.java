package com.example.AppEnglish.modules.pronunciation.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AzurePronunciationAssessmentService {

    private final ObjectMapper objectMapper;

    @Value("${azure.speech.key}")
    private String speechKey;

    @Value("${azure.speech.region}")
    private String speechRegion;

    @Value("${azure.speech.locale:en-US}")
    private String locale;

    @Value("${azure.speech.timeout-seconds:20}")
    private long timeoutSeconds;

    public Map<String, Object> assess(File wavFile, String referenceText) {
        try (SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechKey, speechRegion);
             AudioConfig audioConfig = AudioConfig.fromWavFileInput(wavFile.getAbsolutePath());
             SpeechRecognizer recognizer = new SpeechRecognizer(speechConfig, audioConfig)) {

            speechConfig.setSpeechRecognitionLanguage(locale);

            PronunciationAssessmentConfig assessmentConfig = new PronunciationAssessmentConfig(
                    referenceText,
                    PronunciationAssessmentGradingSystem.HundredMark,
                    PronunciationAssessmentGranularity.Word,
                    true
            );
            assessmentConfig.enableProsodyAssessment();
            assessmentConfig.applyTo(recognizer);

            SpeechRecognitionResult result = recognizer.recognizeOnceAsync().get(timeoutSeconds, TimeUnit.SECONDS);

            Map<String, Object> out = new HashMap<>();
            out.put("recognizedText", result.getText());

            String json = result.getProperties().getProperty(PropertyId.SpeechServiceResponse_JsonResult);
            if (json != null && !json.isBlank()) {
                JsonNode root = objectMapper.readTree(json);
                JsonNode nbest0 = root.path("NBest").isArray() && root.path("NBest").size() > 0
                        ? root.path("NBest").get(0)
                        : null;

                if (nbest0 != null) {
                    out.put("accuracyScore", nbest0.path("AccuracyScore").isNumber() ? nbest0.path("AccuracyScore").asDouble() : null);
                    out.put("fluencyScore", nbest0.path("FluencyScore").isNumber() ? nbest0.path("FluencyScore").asDouble() : null);
                    out.put("prosodyScore", nbest0.path("ProsodyScore").isNumber() ? nbest0.path("ProsodyScore").asDouble() : null);

                    JsonNode pa = nbest0.path("PronunciationAssessment");
                    if (!pa.isMissingNode()) {
                        out.put("pronunciationScore", pa.path("PronScore").isNumber() ? pa.path("PronScore").asDouble() : null);
                        out.put("completenessScore", pa.path("CompletenessScore").isNumber() ? pa.path("CompletenessScore").asDouble() : null);
                    }

                    out.put("raw", objectMapper.convertValue(nbest0, Map.class));
                }
            }

            return out;
        } catch (Exception e) {
            throw new RuntimeException("Azure pronunciation assessment failed: " + e.getMessage(), e);
        }
    }
}


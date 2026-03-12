package com.example.appenglish.modules.pronunciation.service;

import com.example.appenglish.modules.pronunciation.dto.AzureAssessmentResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.audio.AudioInputStream;
import com.microsoft.cognitiveservices.speech.audio.AudioStreamFormat;
import com.microsoft.cognitiveservices.speech.audio.PushAudioInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AzureStreamingPronunciationService {

    private final ObjectMapper objectMapper;
    private final AudioFfmpegTranscoder ffmpegTranscoder;
    private final AudioFfmpegFileTranscoder ffmpegFileTranscoder;

    @Value("${azure.speech.key}")
    private String speechKey;

    @Value("${azure.speech.region}")
    private String speechRegion;

    @Value("${azure.speech.locale:en-US}")
    private String locale;

    @Value("${azure.speech.timeout-seconds:20}")
    private long timeoutSeconds;

    @Value("${app.temp-dir:temp}")
    private String tempDir;

    @Value("${app.pronunciation.ffmpeg-file-fallback:true}")
    private boolean ffmpegFileFallback;

    public AzureAssessmentResult assess(byte[] audioBytes, String referenceText, String fileSuffix) {
        try (SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechKey, speechRegion)) {

            speechConfig.setSpeechRecognitionLanguage(locale);

            AudioStreamFormat format = AudioStreamFormat.getWaveFormatPCM(16000, (short) 16, (short) 1);

            if (shouldForceFileMode(fileSuffix)) {
                return runAssessmentWithTempFile(speechConfig, format, audioBytes, referenceText, fileSuffix);
            }

            AzureAssessmentResult pipeAttempt;
            try {
                pipeAttempt = runAssessmentWithFfmpegPipe(speechConfig, format, audioBytes, referenceText);
            } catch (Exception e) {
                if (!ffmpegFileFallback) {
                    throw e;
                }
                log.warn("ffmpeg pipe failed, fallback to file mode: {}", e.getMessage());
                return runAssessmentWithTempFile(speechConfig, format, audioBytes, referenceText, fileSuffix);
            }

            Map<String, Object> fb = pipeAttempt.getRawFeedback() == null ? new HashMap<>() : new HashMap<>(pipeAttempt.getRawFeedback());
            String ffmpegErr = fb.get("ffmpegErr") == null ? null : String.valueOf(fb.get("ffmpegErr"));
            long pcmBytes = fb.get("pcmBytes") instanceof Number n ? n.longValue() : 0L;

            boolean shouldFallback = ffmpegFileFallback
                    && pcmBytes == 0
                    && ffmpegErr != null
                    && (ffmpegErr.contains("partial file") || ffmpegErr.contains("Invalid data"));

            if (!shouldFallback) {
                return pipeAttempt;
            }

            return runAssessmentWithTempFile(speechConfig, format, audioBytes, referenceText, fileSuffix);

        } catch (Exception e) {
            throw new RuntimeException("Azure pronunciation assessment failed: " + e.getMessage(), e);
        }
    }

    private AzureAssessmentResult runAssessmentWithFfmpegPipe(SpeechConfig speechConfig,
                                                             AudioStreamFormat format,
                                                             byte[] audioBytes,
                                                             String referenceText) throws Exception {

        PushAudioInputStream pushStream = AudioInputStream.createPushStream(format);
        AudioConfig audioConfig = AudioConfig.fromStreamInput(pushStream);
        CompletableFuture<SpeechRecognitionResult> resultFuture = startRecognition(speechConfig, audioConfig, pushStream, referenceText);

        AudioFfmpegTranscoder.TranscodeResult tr = ffmpegTranscoder.transcodeToPcm16kMono(new java.io.ByteArrayInputStream(audioBytes));
        long pcmBytes = pipePcmToAzure(tr.pcmStream(), pushStream);
        int ffmpegExit = waitForFfmpegExit(tr.process(), tr.waitTimeoutSeconds());
        String ffmpegErr = tr.errFuture().join();

        SpeechRecognitionResult result = resultFuture.get(timeoutSeconds, TimeUnit.SECONDS);

        AzureAssessmentResult parsed = parseResult(result);

        Map<String, Object> fb = parsed.getRawFeedback() == null ? new HashMap<>() : new HashMap<>(parsed.getRawFeedback());
        fb.put("ffmpegMode", "pipe");
        fb.put("ffmpegBytesWritten", tr.bytesWritten());
        fb.put("ffmpegExit", ffmpegExit);
        fb.put("ffmpegErr", ffmpegErr);
        fb.put("pcmBytes", pcmBytes);

        return AzureAssessmentResult.builder()
                .reason(parsed.getReason())
                .recognizedText(parsed.getRecognizedText())
                .pronunciationScore(parsed.getPronunciationScore())
                .accuracyScore(parsed.getAccuracyScore())
                .fluencyScore(parsed.getFluencyScore())
                .prosodyScore(parsed.getProsodyScore())
                .completenessScore(parsed.getCompletenessScore())
                .rawFeedback(fb)
                .cancellationDetails(parsed.getCancellationDetails())
                .build();
    }

    private AzureAssessmentResult runAssessmentWithTempFile(SpeechConfig speechConfig,
                                                            AudioStreamFormat format,
                                                            byte[] audioBytes,
                                                            String referenceText,
                                                            String fileSuffix) throws Exception {

        Path dir = Path.of(tempDir, "pronunciation");
        Files.createDirectories(dir);
        String suffix = (fileSuffix == null || fileSuffix.isBlank()) ? ".dat" : fileSuffix;
        Path tmp = Files.createTempFile(dir, "ffmpeg-input-", suffix);

        try {
            Files.write(tmp, audioBytes);
            return runAssessmentWithFfmpegFile(speechConfig, format, tmp.toFile(), referenceText);
        } finally {
            try {
                Files.deleteIfExists(tmp);
            } catch (Exception e) {
                log.warn("Could not delete temp file {}: {}", tmp, e.getMessage());
            }
        }
    }

    private AzureAssessmentResult runAssessmentWithFfmpegFile(SpeechConfig speechConfig,
                                                             AudioStreamFormat format,
                                                             java.io.File inputFile,
                                                             String referenceText) throws Exception {

        PushAudioInputStream pushStream = AudioInputStream.createPushStream(format);
        AudioConfig audioConfig = AudioConfig.fromStreamInput(pushStream);
        CompletableFuture<SpeechRecognitionResult> resultFuture = startRecognition(speechConfig, audioConfig, pushStream, referenceText);

        AudioFfmpegFileTranscoder.FileTranscodeResult tr = ffmpegFileTranscoder.transcodeFileToPcm16kMono(inputFile);
        long pcmBytes;
        try {
            pcmBytes = pipePcmToAzure(tr.pcmStream(), pushStream);
        } finally {
            int exit = tr.waitForExitCode(20000);
            String err = tr.stderr();
            log.info("ffmpeg(file) exit={} err={}", exit, err == null ? "" : err);
        }

        SpeechRecognitionResult result = resultFuture.get(timeoutSeconds, TimeUnit.SECONDS);
        AzureAssessmentResult parsed = parseResult(result);

        Map<String, Object> fb = parsed.getRawFeedback() == null ? new HashMap<>() : new HashMap<>(parsed.getRawFeedback());
        fb.put("ffmpegMode", "file");
        fb.put("pcmBytes", pcmBytes);
        fb.put("ffmpegExit", tr.waitForExitCode(1));
        fb.put("ffmpegErr", tr.stderr());

        return AzureAssessmentResult.builder()
                .reason(parsed.getReason())
                .recognizedText(parsed.getRecognizedText())
                .pronunciationScore(parsed.getPronunciationScore())
                .accuracyScore(parsed.getAccuracyScore())
                .fluencyScore(parsed.getFluencyScore())
                .prosodyScore(parsed.getProsodyScore())
                .completenessScore(parsed.getCompletenessScore())
                .rawFeedback(fb)
                .cancellationDetails(parsed.getCancellationDetails())
                .build();
    }

    private CompletableFuture<SpeechRecognitionResult> startRecognition(
            SpeechConfig speechConfig,
            AudioConfig audioConfig,
            PushAudioInputStream pushStream,
            String referenceText) {

        return CompletableFuture.supplyAsync(() -> {
            try (SpeechRecognizer recognizer = new SpeechRecognizer(speechConfig, audioConfig);
                 PronunciationAssessmentConfig assessmentConfig = new PronunciationAssessmentConfig(
                         referenceText,
                         PronunciationAssessmentGradingSystem.HundredMark,
                         PronunciationAssessmentGranularity.Phoneme,
                         true
                 )) {

                assessmentConfig.enableProsodyAssessment();
                assessmentConfig.applyTo(recognizer);

                return recognizer.recognizeOnceAsync().get();
            } catch (Exception e) {
                throw new RuntimeException("Speech recognition failed: " + e.getMessage(), e);
            }
        });
    }

    private long pipePcmToAzure(java.io.InputStream pcmStream, PushAudioInputStream pushStream) throws IOException {
        byte[] buffer = new byte[4096];
        int bytesRead;
        long total = 0;
        while ((bytesRead = pcmStream.read(buffer)) != -1) {
            total += bytesRead;
            if (bytesRead == buffer.length) {
                pushStream.write(buffer);
            } else {
                byte[] chunk = new byte[bytesRead];
                System.arraycopy(buffer, 0, chunk, 0, bytesRead);
                pushStream.write(chunk);
            }
        }
        pushStream.close();
        return total;
    }

    private int waitForFfmpegExit(Process process, long timeoutSeconds) throws IOException {
        try {
            boolean exited = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            if (!exited) {
                process.destroyForcibly();
                throw new IOException("ffmpeg process timed out after " + timeoutSeconds + "s");
            }
            return process.exitValue();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("ffmpeg process interrupted", e);
        }
    }

    private boolean shouldForceFileMode(String fileSuffix) {
        if (!ffmpegFileFallback) {
            return false;
        }
        if (fileSuffix == null || fileSuffix.isBlank()) {
            return false;
        }
        String suffix = fileSuffix.toLowerCase();
        return suffix.equals(".webm") || suffix.equals(".ogg");
    }

    private AzureAssessmentResult parseResult(SpeechRecognitionResult result) throws Exception {
        ResultReason reason = result.getReason();

        if (reason == ResultReason.Canceled) {
            CancellationDetails cd = CancellationDetails.fromResult(result);
            String details = cd.getReason() + ":" + cd.getErrorCode() + ":" + cd.getErrorDetails();
            return AzureAssessmentResult.builder()
                    .reason(reason)
                    .recognizedText(result.getText())
                    .cancellationDetails(details)
                    .rawFeedback(Map.of(
                            "reason", String.valueOf(cd.getReason()),
                            "errorCode", String.valueOf(cd.getErrorCode()),
                            "errorDetails", cd.getErrorDetails()
                    ))
                    .build();
        }

        Map<String, Object> raw = null;

        Double pronunciationScore = null;
        Double accuracyScore = null;
        Double fluencyScore = null;
        Double prosodyScore = null;
        Double completenessScore = null;

        String json = result.getProperties().getProperty(PropertyId.SpeechServiceResponse_JsonResult);
        if (json != null && !json.isBlank()) {
            JsonNode root = objectMapper.readTree(json);
            JsonNode nbest0 = root.path("NBest").isArray() && root.path("NBest").size() > 0
                    ? root.path("NBest").get(0)
                    : null;

            if (nbest0 != null) {
                JsonNode pa = nbest0.path("PronunciationAssessment");
                if (!pa.isMissingNode()) {
                    pronunciationScore = getDouble(pa, "PronScore");
                    accuracyScore = getDouble(pa, "AccuracyScore");
                    fluencyScore = getDouble(pa, "FluencyScore");
                    prosodyScore = getDouble(pa, "ProsodyScore");
                    completenessScore = getDouble(pa, "CompletenessScore");
                }

                raw = objectMapper.convertValue(nbest0, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
            }
        }

        if (raw == null) {
            raw = new HashMap<>();
        }
        raw.putIfAbsent("resultReason", String.valueOf(reason));

        return AzureAssessmentResult.builder()
                .reason(reason)
                .recognizedText(result.getText())
                .accuracyScore(accuracyScore)
                .fluencyScore(fluencyScore)
                .prosodyScore(prosodyScore)
                .pronunciationScore(pronunciationScore)
                .completenessScore(completenessScore)
                .rawFeedback(raw)
                .build();
    }

    private Double getDouble(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isNumber() ? value.asDouble() : null;
    }
}

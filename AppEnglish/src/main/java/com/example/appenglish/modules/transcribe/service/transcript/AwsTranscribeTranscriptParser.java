package com.example.AppEnglish.modules.transcribe.service.transcript;

import com.example.AppEnglish.modules.transcribe.dto.SplitOptions;
import com.example.AppEnglish.modules.transcribe.dto.TranscriptSentence;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AwsTranscribeTranscriptParser {

    ObjectMapper objectMapper;

    public List<TranscriptSentence> parseSentences(String awsTranscriptJson) {
        return parseSentences(awsTranscriptJson, SplitOptions.defaultOptions());
    }

    public List<TranscriptSentence> parseSentences(String awsTranscriptJson, SplitOptions options) {
        if (awsTranscriptJson == null || awsTranscriptJson.isBlank()) {
            return Collections.emptyList();
        }

        try {
            JsonNode root = objectMapper.readTree(awsTranscriptJson);
            JsonNode items = root.path("results").path("items");
            if (!items.isArray()) {
                return Collections.emptyList();
            }

            SplitOptions opt = options != null ? options : SplitOptions.defaultOptions();

            List<TranscriptSentence> sentences = new ArrayList<>();

            StringBuilder currentText = new StringBuilder();
            Double sentenceStart = null;
            Double lastEnd = null;
            Double lastPronunciationEnd = null;
            int sentenceIndex = 0;

            for (JsonNode item : items) {
                String type = item.path("type").asText("");
                JsonNode alternatives = item.path("alternatives");
                String content = alternatives.isArray() && alternatives.size() > 0
                        ? alternatives.get(0).path("content").asText("")
                        : "";

                if (content.isBlank()) {
                    continue;
                }

                if ("pronunciation".equals(type)) {
                    double start = item.path("start_time").asDouble(-1);
                    double end = item.path("end_time").asDouble(-1);

                    if (opt.getSplitOnSilenceGapMs() != null
                            && lastPronunciationEnd != null
                            && start >= 0
                            && (start - lastPronunciationEnd) * 1000.0 >= opt.getSplitOnSilenceGapMs()
                            && currentText.length() > 0) {
                        flushSentence(sentences, currentText, sentenceStart, lastEnd, sentenceIndex++);
                        sentenceStart = null;
                        lastEnd = null;
                    }

                    if (sentenceStart == null && start >= 0) {
                        sentenceStart = start;
                    }
                    lastEnd = end >= 0 ? end : lastEnd;
                    lastPronunciationEnd = end >= 0 ? end : lastPronunciationEnd;

                    if (currentText.length() > 0) {
                        currentText.append(' ');
                    }
                    currentText.append(content);

                    if (opt.getMaxChars() != null && currentText.length() >= opt.getMaxChars()) {
                        flushSentence(sentences, currentText, sentenceStart, lastEnd, sentenceIndex++);
                        sentenceStart = null;
                        lastEnd = null;
                    }
                } else if ("punctuation".equals(type)) {
                    currentText.append(content);

                    if (shouldFlushOnPunctuation(content, opt)) {
                        flushSentence(sentences, currentText, sentenceStart, lastEnd, sentenceIndex++);
                        sentenceStart = null;
                        lastEnd = null;
                    } else if (opt.getMaxChars() != null && currentText.length() >= opt.getMaxChars()) {
                        flushSentence(sentences, currentText, sentenceStart, lastEnd, sentenceIndex++);
                        sentenceStart = null;
                        lastEnd = null;
                    }
                }
            }

            flushSentence(sentences, currentText, sentenceStart, lastEnd, sentenceIndex++);
            return sentences;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AWS Transcribe transcript JSON", e);
        }
    }

    private static boolean shouldFlushOnPunctuation(String punctuation, SplitOptions opt) {
        if (punctuation == null || punctuation.isBlank()) {
            return false;
        }

        if (opt.isFlushOnAnyPunctuation()) {
            return true;
        }

        if (opt.isFlushOnComma() && ",".equals(punctuation)) {
            return true;
        }

        return ".".equals(punctuation) || "!".equals(punctuation) || "?".equals(punctuation)
                || "…".equals(punctuation) || "...".equals(punctuation);
    }

    private static void flushSentence(List<TranscriptSentence> out,
                                      StringBuilder text,
                                      Double start,
                                      Double end,
                                      int sentenceIndex) {
        String s = text.toString().trim();
        if (!s.isBlank()) {
            Integer startMs = start != null ? (int) Math.round(start * 1000) : null;
            Integer endMs = end != null ? (int) Math.round(end * 1000) : null;
            TranscriptSentence ts = new TranscriptSentence();
            ts.setText(s);
            ts.setStartMs(startMs);
            ts.setEndMs(endMs);
            ts.setSentenceIndex(sentenceIndex);
            out.add(ts);
        }
        text.setLength(0);
    }
}


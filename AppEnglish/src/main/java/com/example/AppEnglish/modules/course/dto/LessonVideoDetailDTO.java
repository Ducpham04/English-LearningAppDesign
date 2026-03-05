package com.example.AppEnglish.modules.course.dto;

import com.example.AppEnglish.entity.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DTO for GET /v1/lessons/{lessonId}/video
 * Returns video-specific details for a lesson: URL, subtitles, and target
 * words.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LessonVideoDetailDTO {

    private Long lessonId;
    private String title;
    private String videoUrl;
    private Integer duration;
    private List<SubtitleDTO> subtitles;
    private List<String> targetWords;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubtitleDTO {
        private Long id;
        private String text;
        private double startTime;
        private double endTime;
        private List<WordDetailDTO> words;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WordDetailDTO {
        private String text;
        private double startTime;
        private double endTime;
        private String pronunciation;
        private String meaning;
    }

    // ── Factory ──────────────────────────────────────────────────

    public static LessonVideoDetailDTO fromEntity(Lesson lesson) {
        LessonVideoDetailDTOBuilder builder = LessonVideoDetailDTO.builder()
                .lessonId(lesson.getId())
                .title(lesson.getTitle());

        Map<String, Object> payload = lesson.getContentPayload();
        if (payload != null && payload.get("duration") != null) {
            builder.duration(((Number) payload.get("duration")).intValue());
        }

        builder.targetWords(parseTargetWords(payload));

        LessonVideo video = lesson.getVideo();
        if (video != null) {
            builder.videoUrl(video.getVideoUrl());
            builder.subtitles(parseSubtitles(video.getTranscriptJson()));
        }

        return builder.build();
    }

    // ── Helpers (same logic as LessonDetailDTO) ─────────────────

    @SuppressWarnings("unchecked")
    public static List<SubtitleDTO> parseSubtitles(List<Map<String, Object>> transcriptJson) {
        if (transcriptJson == null || transcriptJson.isEmpty()) {
            return null;
        }

        List<SubtitleDTO> subtitles = new ArrayList<>();
        long idCounter = 1;

        for (Map<String, Object> entry : transcriptJson) {
            double startTime = toDouble(entry.get("start"));
            double endTime = toDouble(entry.get("end"));
            String text = (String) entry.get("text");

            List<WordDetailDTO> wordDetails = new ArrayList<>();
            Object wordsObj = entry.get("words");
            if (wordsObj instanceof List<?> wordsList) {
                for (Object wordObj : wordsList) {
                    if (wordObj instanceof Map<?, ?> wordMap) {
                        wordDetails.add(WordDetailDTO.builder()
                                .text((String) wordMap.get("text"))
                                .startTime(toDouble(wordMap.get("startTime")))
                                .endTime(toDouble(wordMap.get("endTime")))
                                .pronunciation((String) wordMap.get("pronunciation"))
                                .meaning((String) wordMap.get("meaning"))
                                .build());
                    }
                }
            }

            subtitles.add(SubtitleDTO.builder()
                    .id(idCounter++)
                    .text(text)
                    .startTime(startTime)
                    .endTime(endTime)
                    .words(wordDetails.isEmpty() ? null : wordDetails)
                    .build());
        }

        return subtitles;
    }

    public static List<String> parseTargetWords(Map<String, Object> payload) {
        if (payload != null && payload.get("targetWords") instanceof List<?> tw) {
            return tw.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .collect(Collectors.toList());
        }
        return null;
    }

    private static double toDouble(Object value) {
        if (value instanceof Number num) {
            return num.doubleValue();
        }
        return 0.0;
    }
}

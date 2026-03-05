package com.example.AppEnglish.modules.course.dto;

import com.example.AppEnglish.entity.*;
import com.example.AppEnglish.enums.LessonType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LessonDetailDTO {
    private Long id;
    private Long sessionId;
    private String title;
    private String type;
    private String description;
    private Integer duration;
    private String status;
    private Integer order;

    private String videoUrl;
    private List<SubtitleDTO> subtitles;
    private List<String> targetWords;
    private Integer minScore;
    private Integer userScore;
    private LocalDateTime completedAt;

    private QuizDetailDTO quiz;

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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuizDetailDTO {
        private Long testId;
        private String title;
        private Integer duration;
        private Integer passScore;
        private List<QuestionDTO> questions;

        public static QuizDetailDTO fromEntity(Test test, List<QuestionDTO> questionDTOs) {
            return QuizDetailDTO.builder()
                    .testId(test.getId())
                    .title(test.getTitle())
                    .duration(test.getDuration())
                    .passScore(test.getPassScore())
                    .questions(questionDTOs)
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionDTO {
        private Long id;
        private String content;
        private String type;
        private List<OptionDTO> options;

        public static QuestionDTO fromEntity(TestQuestion question, List<OptionDTO> options) {
            return QuestionDTO.builder()
                    .id(question.getId())
                    .content(question.getContent())
                    .type(question.getType().name())
                    .options(options)
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionDTO {
        private Long id;
        private String content;

        public static OptionDTO fromEntity(TestOption option) {
            return OptionDTO.builder()
                    .id(option.getId())
                    .content(option.getContent())
                    .build();
        }
    }

    public static LessonDetailDTOBuilder baseFromEntity(Lesson lesson) {
        LessonDetailDTOBuilder builder = LessonDetailDTO.builder()
                .id(lesson.getId())
                .sessionId(lesson.getSession() != null ? lesson.getSession().getId() : null)
                .title(lesson.getTitle())
                .type(lesson.getType() != null ? lesson.getType().name().toLowerCase() : null)
                .order(lesson.getOrderIndex());

        Map<String, Object> payload = lesson.getContentPayload();
        if (payload != null) {
            builder.description((String) payload.get("description"));
            if (payload.get("duration") != null) {
                builder.duration(((Number) payload.get("duration")).intValue());
            }
            if (payload.get("minScore") != null) {
                builder.minScore(((Number) payload.get("minScore")).intValue());
            }
        }

        return builder;
    }

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

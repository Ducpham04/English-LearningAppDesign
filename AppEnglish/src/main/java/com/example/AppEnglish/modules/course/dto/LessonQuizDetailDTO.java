package com.example.AppEnglish.modules.course.dto;

import com.example.AppEnglish.entity.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for GET /v1/lessons/{lessonId}/quiz
 * Returns quiz-specific details for a lesson: test info, questions, and
 * options.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LessonQuizDetailDTO {

    private Long lessonId;
    private String lessonTitle;
    private Long testId;
    private String title;
    private Integer duration;
    private Integer passScore;
    private List<QuestionDTO> questions;

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

    public static LessonQuizDetailDTO fromEntity(Long lessonId, String lessonTitle,
            Test test, List<QuestionDTO> questionDTOs) {
        return LessonQuizDetailDTO.builder()
                .lessonId(lessonId)
                .lessonTitle(lessonTitle)
                .testId(test.getId())
                .title(test.getTitle())
                .duration(test.getDuration())
                .passScore(test.getPassScore())
                .questions(questionDTOs)
                .build();
    }
}

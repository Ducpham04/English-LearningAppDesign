package com.example.appenglish.modules.test.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Bài test dùng để làm bài: câu hỏi + đáp án (options) KHÔNG có correctSchema / isCorrect.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestTakeDto {
    private Long id;
    private String title;
    private Integer durationMinutes;
    private Integer passScore;
    private Integer totalPoints;
    private List<QuestionTakeDto> questions;
}

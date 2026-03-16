package com.example.appenglish.modules.test.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttemptResultDto {
    private Long attemptId;
    private Long testId;
    private Integer totalScore;
    private Integer totalPoints;
    private Integer passScore;
    private Boolean passed;
    private Integer correctCount;
    private Integer totalQuestions;
    private Instant submittedAt;
    private List<AnswerResultItem> breakdown;
}

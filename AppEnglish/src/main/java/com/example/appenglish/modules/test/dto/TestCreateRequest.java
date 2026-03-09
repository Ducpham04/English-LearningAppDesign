package com.example.appenglish.modules.test.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestCreateRequest {
    private String title;
    private Integer durationMinutes;
    private Integer passScore;
    private Integer maxAttempts;
    private LocalDateTime deadline;
    private Boolean isFaceRequired;
    private Long courseId;
    private Long lessonId;
}

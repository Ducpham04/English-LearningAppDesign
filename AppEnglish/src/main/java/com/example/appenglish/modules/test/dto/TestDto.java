package com.example.appenglish.modules.test.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Test list item hoặc chi tiết (không chứa đáp án đúng).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestDto {
    private Long id;
    private String title;
    private Integer durationMinutes;
    private Integer passScore;
    private Integer maxAttempts;
    private LocalDateTime deadline;
    private Boolean isFaceRequired;
    private Long courseId;
    private String courseTitle;
    private Long lessonId;
    private Integer questionCount;
    private Integer totalPoints;

    /** Người tạo bài test (quản lý). */
    private String createdById;
    private String createdByName;

    // Cho học sinh: thống kê attempt
    private Integer attemptCount;
    private Integer bestScore;
    private Instant lastAttemptAt;
    private String status; // not-started, in-progress, completed
}

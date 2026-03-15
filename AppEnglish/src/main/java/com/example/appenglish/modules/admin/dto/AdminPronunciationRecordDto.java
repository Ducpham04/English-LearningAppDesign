package com.example.appenglish.modules.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * Một bản ghi phát âm trong báo cáo admin (chấm phát âm, phân tích AI).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminPronunciationRecordDto {
    private Long id;
    private Instant createdAt;

    private String userId;
    private String userEmail;
    private String userFullName;

    private Long lessonId;
    private String lessonTitle;
    private Long sessionId;
    private String sessionTitle;

    private String expectedText;
    private String recognizedText;

    private Double pronunciationScore;
    private Double accuracyScore;
    private Double fluencyScore;
    private Double prosodyScore;
    private Double completenessScore;

    private String status;       // PROCESSING, SCORED, FAILED
    private String s3Status;    // PENDING, UPLOADED, FAILED
    private String errorMessage;
    /** Phản hồi AI (phân tích chi tiết). */
    private Map<String, Object> feedback;
}

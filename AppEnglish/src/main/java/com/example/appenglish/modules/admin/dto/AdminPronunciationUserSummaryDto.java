package com.example.appenglish.modules.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Tổng hợp theo người dùng: số lần thu âm, điểm trung bình (chấm phát âm / AI).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminPronunciationUserSummaryDto {
    private String userId;
    private String userEmail;
    private String userFullName;

    private long attemptCount;
    private Double avgPronunciationScore;
    private Double avgAccuracyScore;
    private Instant lastAttemptAt;
}

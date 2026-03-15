package com.example.appenglish.modules.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Báo cáo tổng quan quản lý nội dung: bài học, video, bản ghi phát âm, người dùng.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminContentReportDto {
    /** Tổng số bài học. */
    private long totalLessons;
    /** Số bài học có video (có bản ghi lesson_videos). */
    private long lessonsWithVideo;
    /** Số bài học chưa có video. */
    private long lessonsWithoutVideo;
    /** Tổng số bản ghi phát âm (pronunciation_attempts). */
    private long totalPronunciationAttempts;
    /** Số người dùng có ít nhất 1 bản ghi phát âm. */
    private long totalUsersWithAttempts;
}

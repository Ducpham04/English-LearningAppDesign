package com.example.appenglish.modules.session.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Chi tiết session kèm thông tin liên quan: bài học, số bản ghi phát âm, tiến độ học viên.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionDetailDto {
    private Long id;
    private String title;
    private Integer orderIndex;
    private Long courseId;
    private String courseTitle;
    private Long topicId;
    private String topicTitle;

    /** Số bài học trong session. */
    private int lessonCount;
    /** Danh sách bài học (tóm tắt). */
    private List<LessonSummaryDto> lessons;

    /** Tổng số bản ghi phát âm (pronunciation_attempts) của tất cả bài học trong session. */
    private long pronunciationAttemptCount;
    /** Số bản ghi tiến độ bài học (user_lesson_progress) trong session. */
    private long userLessonProgressCount;
    /** Số học viên đã hoàn thành session (user_session_progress). */
    private long userSessionProgressCount;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LessonSummaryDto {
        private Long id;
        private String title;
        private Integer orderIndex;
        private String type;
        /** Số bản ghi phát âm của bài học này. */
        private long pronunciationAttemptCount;
    }
}

package com.example.appenglish.modules.student.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Session mà học sinh đã/đang tham gia (có ít nhất 1 bài đã hoàn thành).
 * Dùng cho Dashboard "Đang học".
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionProgressDto {
    private Long sessionId;
    private String title;
    private Long topicId;
    private String topicName;
    private int completedLessons;
    private int totalLessons;
    private boolean sessionCompleted;
}

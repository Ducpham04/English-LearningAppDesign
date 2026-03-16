package com.example.appenglish.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Lưu tiến độ học sinh: đã hoàn thành bài học nào, điểm (nếu có).
 */
@Entity
@Table(name = "user_lesson_progress", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "user_id", "lesson_id" })
})
@Getter
@Setter
public class UserLessonProgress extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;

    @Column(name = "score")
    private Integer score;
}

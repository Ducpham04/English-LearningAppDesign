package com.example.appenglish.entity;

import com.example.appenglish.enums.LearningStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_lesson_progress", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "lesson_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false) // Fix: Đổi thành false hoặc xóa callSuper nếu BaseEntity là lớp trừu tượng đơn giản
public class UserLessonProgress extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LearningStatus status; // STARTED, COMPLETED

    private Integer lastVideoPosition;

    private Double quizScore;

    private LocalDateTime completedAt;

    @PreUpdate
    @PrePersist
    public void updateTimestamp() {
        if (this.status == LearningStatus.COMPLETED && this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        }
    }
}
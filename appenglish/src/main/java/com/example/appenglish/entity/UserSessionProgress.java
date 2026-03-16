package com.example.appenglish.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Lưu tiến độ học sinh: đã hoàn thành session nào (khi hoàn thành hết các bài học trong session).
 */
@Entity
@Table(name = "user_session_progress", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "user_id", "session_id" })
})
@Getter
@Setter
public class UserSessionProgress extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;
}

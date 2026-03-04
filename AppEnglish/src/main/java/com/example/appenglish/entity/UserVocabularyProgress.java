package com.example.appenglish.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * TRÁI TIM CỦA HỆ THỐNG SRS.
 * Lưu tiến độ học của 1 User với 1 Từ vựng.
 * Áp dụng thuật toán SuperMemo-2 (SM-2).
 */
@Entity
@Table(name = "user_vocabulary_progress")
@Getter @Setter
public class UserVocabularyProgress extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vocabulary_id", nullable = false)
    private Vocabulary vocabulary;

    @Column(name = "status")
    private String status; // NEW, LEARNING, MASTERED

    @Column(name = "times_reviewed")
    private Integer timesReviewed = 0;

    @Column(name = "correct_count")
    private Integer correctCount = 0;

    // Optional fields (not always present in seed) - keep nullable
    @Column(name = "next_review_at")
    private LocalDateTime nextReviewAt;
}
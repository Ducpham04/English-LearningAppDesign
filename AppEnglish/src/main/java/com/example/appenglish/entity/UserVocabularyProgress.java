package com.example.AppEnglish.entity;

import com.example.AppEnglish.enums.LearningStatus;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * TRÁI TIM CỦA HỆ THỐNG SRS.
 * Lưu tiến độ học của 1 User với 1 Từ vựng.
 * Áp dụng thuật toán SuperMemo-2 (SM-2).
 */
@Entity
@Table(name = "user_vocabulary_progress")
@Getter @Setter
public class UserVocabularyProgress {

    @EmbeddedId
    private UserVocabId id; // Composite Key (User + Vocab)

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("vocabId")
    private Vocabulary vocabulary;

    @Enumerated(EnumType.STRING)
    private LearningStatus status; // NEW, LEARNING, MASTERED

    // --- THUẬT TOÁN SM-2 ---
    @Column(name = "easiness_factor")
    private Double easinessFactor = 2.5; // Độ khó của từ (càng cao càng dễ)

    @Column(name = "interval_days")
    private Integer intervalDays = 0; // Số ngày chờ đến lần ôn tiếp

    @Column(name = "next_review_at")
    private LocalDateTime nextReviewAt; // Ngày giờ chính xác phải ôn tập
}

@Embeddable
@Getter @Setter @EqualsAndHashCode
class UserVocabId implements Serializable {
    private Long userId;
    private Long vocabId;
}


package com.example.appenglish.entity;

import com.example.appenglish.enums.LearningStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_vocabulary_progress")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVocabularyProgress {

    @EmbeddedId
    private UserVocabId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId") // Khớp với biến trong UserVocabId
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("vocabularyId") // Khớp với biến trong UserVocabId
    @JoinColumn(name = "vocabulary_id")
    private Vocabulary vocabulary;

    @Enumerated(EnumType.STRING)
    private LearningStatus status;

    @Builder.Default
    private Integer reviewCount = 0;

    private LocalDateTime lastReviewedAt;
    private LocalDateTime nextReviewAt;
}
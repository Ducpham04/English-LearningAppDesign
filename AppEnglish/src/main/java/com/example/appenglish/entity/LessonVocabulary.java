package com.example.AppEnglish.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

/**
 * Bảng trung gian Lesson – Vocabulary (chuẩn hóa).
 * Cho phép tra cứu: từ vựng thuộc bài học nào, bài học có những từ nào.
 */
@Entity
@Table(name = "lesson_vocabularies", indexes = {
        @Index(name = "idx_lesson_vocab_lesson", columnList = "lesson_id"),
        @Index(name = "idx_lesson_vocab_vocab", columnList = "vocab_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LessonVocabulary {

    @EmbeddedId
    private LessonVocabularyId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("lessonId")
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("vocabId")
    @JoinColumn(name = "vocab_id", nullable = false)
    private Vocabulary vocabulary;

    @Column(name = "order_index")
    private Integer orderIndex; // Thứ tự từ trong bài (tùy chọn)

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class LessonVocabularyId implements Serializable {
        private Long lessonId;
        private Long vocabId;
    }
}

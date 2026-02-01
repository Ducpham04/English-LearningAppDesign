package com.example.appenglish.entity;

import com.example.appenglish.enums.LessonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bài học (Micro-learning).
 */
@Entity
@Table(name = "lessons", indexes = {
    @Index(name = "idx_lesson_type", columnList = "type"),
    @Index(name = "idx_lesson_session", columnList = "session_id")
})
@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
@Builder
public class Lesson extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description; // Fix lỗi undefined getDescription()

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Curriculum course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LessonType type; // VIDEO, QUIZ, VOCAB, SPEAKING

    @Column(name = "order_index")
    private Integer orderIndex;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "content_payload", columnDefinition = "jsonb")
    @Builder.Default // Bắt buộc phải có để Builder không làm null trường này
    private Map<String, Object> contentPayload = new HashMap<>(); 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private Session session;

    // --- RELATIONS ---

    @OneToOne(mappedBy = "lesson", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private LessonVideo video;

    @OneToOne(mappedBy = "lesson", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Test test;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @Builder.Default // Bắt buộc phải có để tránh NullPointerException khi dùng Builder
    private List<LessonVocabulary> lessonVocabularies = new ArrayList<>();

    /**
     * Helper method để lấy duration từ JSON payload an toàn.
     */
    public Integer getDurationFromPayload() {
        if (contentPayload != null) {
            Object duration = contentPayload.get("duration");
            if (duration instanceof Number) {
                return ((Number) duration).intValue();
            }
        }
        return 0;
    }
}
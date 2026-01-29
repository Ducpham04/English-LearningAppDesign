package com.example.appenglish.entity;

import com.example.appenglish.enums.LessonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

/**
 * Bài học (Micro-learning).
 * ĐIỂM ĐẶC BIỆT: Trường contentPayload (JSONB).
 * Lưu cấu hình linh hoạt cho từng loại bài học.
 */
@Entity
@Table(name = "lessons", indexes = {
    @Index(name = "idx_lesson_type", columnList = "type"),
    @Index(name = "idx_lesson_session", columnList = "session_id")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Lesson extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Curriculum course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LessonType type; // VIDEO, QUIZ, VOCAB, SPEAKING

    @Column(name = "order_index")
    private Integer orderIndex; // Thứ tự bài học trong Session

    // --- JSONB CONFIG ---
    // Lưu các metadata nhỏ như duration, vocab_count, v.v.
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "content_payload", columnDefinition = "jsonb")
    private Map<String, Object> contentPayload;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private Session session;

    // --- RELATIONS ---

    // Kết nối với video nếu type là VIDEO
    @OneToOne(mappedBy = "lesson", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private LessonVideo video;

    // Kết nối với bài kiểm tra nếu type là QUIZ
    @OneToOne(mappedBy = "lesson", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Test test;
}
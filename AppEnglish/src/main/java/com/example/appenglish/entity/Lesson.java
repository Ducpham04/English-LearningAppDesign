package com.example.appenglish.entity;

import com.example.appenglish.enums.LessonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Bài học (Micro-learning).
 * ĐIỂM ĐẶC BIỆT: Trường contentPayload (JSONB).
 * Thay vì tạo nhiều bảng (LessonQuiz, LessonVocab), ta lưu cấu hình vào JSON.
 * VD: type=QUIZ -> payload={"quiz_id": 10}
 * VD: type=VIDEO -> payload={"duration": 300}
 */
@Entity
@Table(name = "lessons")
@Getter @Setter
public class Lesson extends BaseEntity {

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Curriculum course;

    @Enumerated(EnumType.STRING)
    private LessonType type; // VIDEO, QUIZ, VOCAB, SPEAKING

    @Column(name = "order_index")
    private Integer orderIndex; // Thứ tự bài học

    // --- JSONB CONFIG ---
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "content_payload", columnDefinition = "jsonb")
    private Map<String, Object> contentPayload;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private Session session;
    
    @OneToOne(mappedBy = "lesson", cascade = CascadeType.ALL)
    private LessonVideo video;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<LessonVocabulary> lessonVocabularies = new ArrayList<>();
}
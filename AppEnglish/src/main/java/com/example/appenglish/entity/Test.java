package com.example.appenglish.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tests", indexes = {
        @Index(name = "idx_test_course", columnList = "course_id"),
        @Index(name = "idx_test_lesson", columnList = "lesson_id")
})
@Getter @Setter
public class Test extends BaseEntity {
    private String title;
    private Integer duration; // Phút (null = không giới hạn)
    private Integer passScore; // Điểm sàn

    @Column(name = "is_face_required")
    private Boolean isFaceRequired; // Yêu cầu FaceID không?

    /** Bài thi thuộc khóa (VD: thi cuối khóa). Null nếu là quiz độc lập hoặc gắn lesson. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Curriculum course;

    /** Bài quiz thuộc bài học. Null nếu thi cấp khóa hoặc độc lập. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;
}
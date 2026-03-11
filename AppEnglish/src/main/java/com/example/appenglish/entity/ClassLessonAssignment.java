package com.example.appenglish.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Bài tập giao cho lớp: giáo viên gắn lesson vào lớp với title, mô tả, hạn nộp, điểm tối đa.
 */
@Entity
@Table(name = "class_lesson_assignments")
@Getter
@Setter
public class ClassLessonAssignment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private Classroom classroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "due_at")
    private LocalDateTime dueAt;

    @Column(name = "max_score")
    private Integer maxScore;

    @Column(name = "is_required")
    private Boolean isRequired;

    @Column(name = "order_index")
    private Integer orderIndex;
}

package com.example.appenglish.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "class_students")
@Getter
@Setter
public class ClassStudent {
    @EmbeddedId
    private ClassStudentId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("classId")
    @JoinColumn(name = "class_id")
    private Classroom classroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("studentId")
    @JoinColumn(name = "student_id")
    private User student;

    private LocalDateTime joinedAt;
    private Boolean isFaceVerified;
}

package com.example.appenglish.entity;

import com.example.appenglish.enums.ClassStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "classrooms", indexes = @Index(name = "idx_classroom_join_code", columnList = "join_code", unique = true))
@Getter @Setter
public class Classroom extends BaseEntity {
    private String name;

    /** Mã lớp (6 ký tự), học viên nhập mã này để xin vào lớp. */
    @Column(name = "join_code", unique = true, length = 20)
    private String joinCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Curriculum course;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private ClassStatus status;
}
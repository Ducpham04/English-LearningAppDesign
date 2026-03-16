package com.example.appenglish.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/** Yêu cầu học viên xin vào lớp (nhập mã lớp). Giáo viên duyệt hoặc từ chối. */
@Entity
@Table(name = "class_join_requests", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "classroom_id", "student_id" })
})
@Getter
@Setter
public class ClassJoinRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id", nullable = false)
    private Classroom classroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    /** PENDING, APPROVED, REJECTED */
    @Column(nullable = false, length = 20)
    private String status = "PENDING";

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responded_by")
    private User respondedBy;
}

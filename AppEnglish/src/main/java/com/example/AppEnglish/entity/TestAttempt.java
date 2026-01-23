package com.example.appenglish.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "test_attempts")
@Getter @Setter
public class TestAttempt extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private Test test;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private Integer totalScore;
    private LocalDateTime submittedAt;
}

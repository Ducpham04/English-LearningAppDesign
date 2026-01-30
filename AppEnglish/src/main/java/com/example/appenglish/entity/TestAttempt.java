package com.example.appenglish.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "test_attempts")
@Getter 
@Setter 
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestAttempt extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id")
    private Test test;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Double score; // Điểm số (0.0 - 10.0)

    @Column(name = "correct_answers", nullable = false)
    private Integer correctAnswers; // Số câu trả lời đúng
}
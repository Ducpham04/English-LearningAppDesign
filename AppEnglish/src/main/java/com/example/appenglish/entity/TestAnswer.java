package com.example.AppEnglish.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "test_answers")
@Getter @Setter
public class TestAnswer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id")
    private TestAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private TestQuestion question;

    // Đáp án user chọn (Text hoặc ID option)
    @Column(columnDefinition = "TEXT")
    private String userAnswer;

    private Boolean isCorrect;
    private Integer scoreEarned;
}

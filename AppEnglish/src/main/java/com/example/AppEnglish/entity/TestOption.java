package com.example.appenglish.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "test_options")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class TestOption extends BaseEntity {

    @Column(nullable = false)
    private String content; // Đảm bảo tên trường là content

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;
}
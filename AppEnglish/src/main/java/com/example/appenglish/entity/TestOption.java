package com.example.AppEnglish.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "test_options")
@Getter
@Setter
public class TestOption extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private TestQuestion question;

    private String content; // Nội dung đáp án A, B...
    private Boolean isCorrect; // Đúng/Sai
}

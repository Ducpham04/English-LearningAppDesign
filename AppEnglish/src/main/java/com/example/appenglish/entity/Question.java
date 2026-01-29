package com.example.appenglish.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "questions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Question extends BaseEntity {

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // Nội dung câu hỏi

    private String image; // URL hình ảnh minh họa (nếu có)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id")
    private Test test; // Thuộc bài Test nào

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
private List<TestOption> options;
}
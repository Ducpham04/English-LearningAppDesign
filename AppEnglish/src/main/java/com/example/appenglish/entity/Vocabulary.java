package com.example.appenglish.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * Thực thể Từ vựng (Vocabulary)
 * Đã loại bỏ liên kết trực tiếp tới Lesson để đơn giản hóa cấu trúc.
 */
@Entity
@Table(name = "vocabularies", indexes = {
    @Index(name = "idx_vocab_word", columnList = "word")
})
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vocabulary extends BaseEntity {

    @Column(nullable = false)
    private String word;

    private String phonetic;

    @Column(columnDefinition = "TEXT")
    private String definition;

    @Column(columnDefinition = "TEXT")
    private String example;

    @Column(length = 20)
    private String level; // Ví dụ: A1, B2, IELTS, Oxford_3000

    /**
     * Tags mở rộng sử dụng kiểu dữ liệu JSONB của PostgreSQL.
     * Cho phép lưu trữ linh hoạt các nhãn như: ["Food", "Travel", "IELTS_Speaking"]
     */
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private List<String> tags = new ArrayList<>();
}
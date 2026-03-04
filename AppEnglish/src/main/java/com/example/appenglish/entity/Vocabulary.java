package com.example.appenglish.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Kho từ vựng Global.
 * Dùng JSONB cho 'meanings' để lưu nhiều nghĩa + ví dụ linh hoạt.
 */
@Entity
@Table(name = "vocabularies", indexes = {
        @Index(name = "idx_vocab_word", columnList = "word") // Tra từ điển nhanh
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vocabulary extends BaseEntity {

    @Column(nullable = false)
    private String word;

    private String phonetic; // Phiên âm IPA
    private String audioUrl; // Phát âm chuẩn

    // Cấu trúc: [{"pos": "noun", "def": "...", "ex": "..."}]
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<Map<String, String>> meanings;

    // Cấu trúc: ["IELTS", "Food", "A1"]
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<String> tags;

    @OneToMany(mappedBy = "vocabulary")
    @Builder.Default
    private List<LessonVocabulary> lessonVocabularies = new ArrayList<>();
}

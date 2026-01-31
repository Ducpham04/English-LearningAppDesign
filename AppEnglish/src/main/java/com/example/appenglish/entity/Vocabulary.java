package com.example.appenglish.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * Kho từ vựng Global.
 * Kết hợp giữa các trường cơ bản (Word, Level) và mở rộng (Tags, Relations).
 */
@Entity
@Table(name = "vocabularies")
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

    // --- GIỮ LẠI TỪ HEAD (Để tương thích với VocabularyService cũ) ---
    
    @Column(columnDefinition = "TEXT")
    private String example;

    private String level; // VD: A1, B1, C2

    // --- THÊM TỪ NHÁNH DEVELOP (Tính năng mới) ---

    // Cấu trúc: ["IELTS", "Food", "Topic_Travel"]
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<String> tags;

    // Liên kết với bài học (Lesson)
    @OneToMany(mappedBy = "vocabulary", fetch = FetchType.LAZY)
    @Builder.Default
    private List<LessonVocabulary> lessonVocabularies = new ArrayList<>();
}
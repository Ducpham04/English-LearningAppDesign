package com.example.AppEnglish.entity;

import com.example.AppEnglish.enums.QuestionType;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import java.util.Map;

/**
 * Ngân hàng câu hỏi.
 * QUAN TRỌNG: 'correctSchema' chứa đáp án đúng, TUYỆT ĐỐI không trả về Frontend.
 */
@Entity
@Table(name = "test_questions")
@Getter @Setter
public class TestQuestion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id")
    private Test test;

    @Column(columnDefinition = "TEXT")
    private String content; // Nội dung câu hỏi (HTML/Text)

    @Enumerated(EnumType.STRING)
    private QuestionType type; // MCQ, FILL_BLANK

    // --- JSONB ANSWER KEY ---
    // VD MCQ: {"correct_option_id": 2}
    // VD Fill: {"accepted": ["apple", "Apple"]}
    @Type(JsonType.class)
    @Column(name = "correct_schema", columnDefinition = "jsonb")
    private Map<String, Object> correctSchema;
}

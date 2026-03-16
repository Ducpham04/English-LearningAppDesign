package com.example.appenglish.modules.test.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestQuestionCreateRequest {
    private String content;
    private String type; // MCQ, FILL_BLANK, SPEAKING
    private Integer orderIndex;
    private Integer points;
    /** MCQ: {"correct_option_id": 2}. FILL_BLANK: {"accepted": ["a","b"]}. SPEAKING: {} hoặc null. */
    private Map<String, Object> correctSchema;
    /** Chỉ số (0-based) đáp án đúng cho MCQ; backend sẽ chuyển thành correct_option_id sau khi tạo options. */
    private Integer correctOptionIndex;
    private List<TestOptionCreateRequest> options;
}

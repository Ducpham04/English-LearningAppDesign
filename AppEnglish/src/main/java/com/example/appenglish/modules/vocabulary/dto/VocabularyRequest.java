package com.example.appenglish.modules.vocabulary.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VocabularyRequest {
    @NotBlank(message = "Word is required")
    private String word;

    private String phonetic;

    @NotBlank(message = "Definition is required")
    private String definition;

    private String example;

    private String level;
    // Đã xóa lessonId ở đây
}
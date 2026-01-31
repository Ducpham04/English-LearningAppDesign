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
    
    @NotBlank(message = "Từ vựng không được để trống")
    private String word;

    private String phonetic;

    @NotBlank(message = "Giải nghĩa không được để trống")
    private String definition;

    private String example;

    private String level;

    private Long lessonId;
}
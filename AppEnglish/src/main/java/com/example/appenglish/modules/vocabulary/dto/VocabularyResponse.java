package com.example.appenglish.modules.vocabulary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class VocabularyResponse {
    private Long id; // Tự động trả về vocabId (1, 2, 3...)
    private String word;
    private String phonetic;
    private String definition;
    private String example;
    private String level;
}
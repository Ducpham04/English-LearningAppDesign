package com.example.appenglish.modules.vocabulary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VocabularyRequest {
    private String word;
    private String phonetic;
    private String definition;
    private String example;
    private String level;
    // Đã xóa hoàn toàn userId và id (vì id tự sinh)
}
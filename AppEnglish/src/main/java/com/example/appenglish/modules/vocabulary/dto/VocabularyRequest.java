package com.example.appenglish.modules.vocabulary.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class VocabularyRequest {
    private String word;
    private String level; // EASY, MEDIUM, HARD
    private Long userId;
}
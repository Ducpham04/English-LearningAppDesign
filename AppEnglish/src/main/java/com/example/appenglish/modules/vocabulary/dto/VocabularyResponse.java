package com.example.appenglish.modules.vocabulary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class VocabularyResponse {
    @JsonProperty("vocabId")
    private Long id;
    private String word;
    private String level;
    private Long userId;
}
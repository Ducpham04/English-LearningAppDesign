package com.example.appenglish.modules.vocabulary.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddToMyVocabularyResponseDto {
    private Long vocabularyId;
    private String word;
    private boolean alreadySaved;
}

package com.example.appenglish.modules.vocabulary.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class MyVocabularyItemDto {
    private Long id;
    private String word;
    private String phonetic;
    private List<Map<String, String>> meanings;
    private String example; // first example from meanings if any
}

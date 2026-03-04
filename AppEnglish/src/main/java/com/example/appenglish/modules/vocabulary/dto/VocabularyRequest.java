package com.example.appenglish.modules.vocabulary.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VocabularyRequest {
    @NotBlank(message = "Word is required")
    private String word;

    private String phonetic;
    private String audioUrl;
    private List<Map<String, String>> meanings;
    private List<String> tags;
}

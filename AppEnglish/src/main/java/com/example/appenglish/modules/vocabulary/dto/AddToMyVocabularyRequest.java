package com.example.appenglish.modules.vocabulary.dto;

import lombok.Data;

@Data
public class AddToMyVocabularyRequest {
    private String word;
    private String phonetic;
    private String meaning;
    /** Câu ví dụ (câu gốc + câu dịch, ví dụ: "It's late now. — Bây giờ đã muộn rồi.") */
    private String example;
    private Long lessonId;
}

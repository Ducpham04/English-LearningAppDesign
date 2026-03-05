package com.example.AppEnglish.modules.vocabulary.dto;

import com.example.AppEnglish.entity.UserVocabularyProgress;
import com.example.AppEnglish.entity.Vocabulary;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewVocabularyDTO {
    private Long id;
    private String word;
    private String meaning;
    private String pronunciation;
    private String imageUrl;
    private LocalDateTime nextReviewDate;
    private Integer reviewCount;
    private Integer mastery;

    public static ReviewVocabularyDTO fromEntity(UserVocabularyProgress progress) {
        Vocabulary vocab = progress.getVocabulary();

        String meaning = null;
        if (vocab.getMeanings() != null && !vocab.getMeanings().isEmpty()) {
            Map<String, String> firstMeaning = vocab.getMeanings().get(0);
            meaning = firstMeaning.get("def");
        }

        return ReviewVocabularyDTO.builder()
                .id(vocab.getId())
                .word(vocab.getWord())
                .meaning(meaning)
                .pronunciation(vocab.getPhonetic())
                .imageUrl(vocab.getImageUrl())
                .nextReviewDate(progress.getNextReviewAt())
                .reviewCount(progress.getIntervalDays())
                .mastery(VocabularyDTO.calculateMastery(progress))
                .build();
    }
}

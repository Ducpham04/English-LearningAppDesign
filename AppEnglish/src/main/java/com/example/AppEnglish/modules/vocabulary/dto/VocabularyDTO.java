package com.example.AppEnglish.modules.vocabulary.dto;

import com.example.AppEnglish.enums.LearningStatus;
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
public class VocabularyDTO {
    private Long id;
    private String word;
    private String meaning;
    private String pronunciation;
    private String example;
    private String audioUrl;
    private String imageUrl;
    private Boolean learned;
    private Integer reviewCount;
    private LocalDateTime lastReviewed;
    private Integer mastery;
    private Long sessionId;

    public static VocabularyDTO fromEntity(Vocabulary vocab, UserVocabularyProgress progress) {

        String primaryMeaning = null;
        String example = null;
        if (vocab.getMeanings() != null && !vocab.getMeanings().isEmpty()) {
            Map<String, String> firstMeaning = vocab.getMeanings().get(0);
            primaryMeaning = firstMeaning.get("def");
            example = firstMeaning.get("ex");
        }

        Boolean isLearned = null;
        Integer reviewCount = null;
        Integer mastery = null;
        LocalDateTime lastReviewed = null;

        if (progress != null) {
            isLearned = progress.getStatus() == LearningStatus.MASTERED;
            mastery = calculateMastery(progress);
            reviewCount = progress.getIntervalDays();
            if (progress.getNextReviewAt() != null && progress.getIntervalDays() != null) {
                lastReviewed = progress.getNextReviewAt().minusDays(progress.getIntervalDays());
            }
        }

        Long sessionId = vocab.getSession() != null ? vocab.getSession().getId() : null;

        return VocabularyDTO.builder()
                .id(vocab.getId())
                .word(vocab.getWord())
                .meaning(primaryMeaning)
                .pronunciation(vocab.getPhonetic())
                .example(example)
                .audioUrl(vocab.getAudioUrl())
                .imageUrl(vocab.getImageUrl())
                .learned(isLearned)
                .reviewCount(reviewCount)
                .lastReviewed(lastReviewed)
                .mastery(mastery)
                .sessionId(sessionId)
                .build();
    }

    public static VocabularyDTO fromEntity(Vocabulary vocab) {
        return fromEntity(vocab, null);
    }

    public static int calculateMastery(UserVocabularyProgress progress) {
        if (progress.getStatus() == LearningStatus.MASTERED) {
            return 100;
        }
        if (progress.getStatus() == LearningStatus.NEW) {
            return 0;
        }

        double ef = progress.getEasinessFactor() != null ? progress.getEasinessFactor() : 2.5;
        int interval = progress.getIntervalDays() != null ? progress.getIntervalDays() : 0;

        int mastery = (int) Math.min(95, interval * 10 + ef * 10);
        return Math.max(0, mastery);
    }
}

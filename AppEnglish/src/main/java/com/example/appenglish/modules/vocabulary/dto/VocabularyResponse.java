package com.example.appenglish.modules.vocabulary.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor 
public class VocabularyResponse {
    private Long id;
    private String word;
    private String meaning;
    private String pronunciation;
    private String example;
    private String audioUrl;
    private String imageUrl;
    private Boolean learned;
    private Integer reviewCount;
    private String lastReviewed;
    private String nextReviewDate; // Thêm trường này cho format 7.2
    private Integer mastery;
    private Long sessionId;
    private String level;

    /**
     * Format 7.1: Wrapper cho danh sách từ vựng chung (có stats & pagination)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VocabularyListWrapper {
        private List<VocabularyResponse> words;
        private PaginationData pagination;
        private StatsData stats;
    }

    /**
     * Format 7.2: Wrapper cho danh sách ôn tập (Review List)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VocabularyReviewWrapper {
        private List<VocabularyResponse> words;
        private long total;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationData {
        private int page;
        private int limit;
        private long total;
        private int totalPages;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatsData {
        private long total;
        private long learned;
        private long learning;
        private long notStarted;
    }
}
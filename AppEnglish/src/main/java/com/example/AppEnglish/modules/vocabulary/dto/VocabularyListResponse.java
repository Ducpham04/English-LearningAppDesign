package com.example.AppEnglish.modules.vocabulary.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.domain.Page;

/**
 * Wrapper response cho GET /vocabulary
 * Uses Spring Page for pagination, plus vocabulary stats.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VocabularyListResponse {
    private Page<VocabularyDTO> words;
    private VocabStatsDTO stats;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VocabStatsDTO {
        private long total;
        private long learned;
        private long learning;
        private long notStarted;
    }
}

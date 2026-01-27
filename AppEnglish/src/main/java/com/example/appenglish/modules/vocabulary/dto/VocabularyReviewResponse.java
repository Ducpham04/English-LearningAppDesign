package com.example.appenglish.modules.vocabulary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO cho UserVocabularyProgress - Từ cần ôn tập.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "VocabularyReviewResponse", description = "Từ vựng cần ôn tập")
public class VocabularyReviewResponse {

    @Schema(description = "ID từ vựng", example = "1")
    private Long vocabularyId;

    @Schema(description = "Từ vựng", example = "hello")
    private String word;

    @Schema(description = "Phiên âm IPA", example = "həˈloʊ")
    private String phonetic;

    @Schema(description = "Link phát âm")
    private String audioUrl;

    @Schema(description = "Trạng thái học", example = "LEARNING")
    private String status;

    @Schema(description = "Độ khó (easiness factor)", example = "2.5")
    private Double easinessFactor;

    @Schema(description = "Số ngày chờ đến lần ôn tiếp", example = "3")
    private Integer intervalDays;

    @Schema(description = "Ngày giờ phải ôn tập", example = "2026-01-27T10:00:00")
    private String nextReviewAt;

    @Schema(description = "Ngày tạo", example = "2026-01-27T10:00:00")
    private String createdAt;
}

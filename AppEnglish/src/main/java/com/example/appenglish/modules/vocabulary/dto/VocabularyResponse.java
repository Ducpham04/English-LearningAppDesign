package com.example.appenglish.modules.vocabulary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * DTO cho Vocabulary (Response).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "VocabularyResponse", description = "Thông tin chi tiết từ vựng")
public class VocabularyResponse {

    @Schema(description = "ID từ vựng", example = "1")
    private Long id;

@Schema(description = "Cấp độ khó của từ", example = "EASY")
private String level;

    @Schema(description = "Từ vựng", example = "hello")
    private String word;

    @Schema(description = "Phiên âm IPA", example = "həˈloʊ")
    private String phonetic;

    @Schema(description = "Link phát âm", example = "https://example.com/audio/hello.mp3")
    private String audioUrl;

    @Schema(description = "Danh sách nghĩa", example = "[{\"pos\": \"noun\", \"def\": \"...\"}]")
    private List<Map<String, String>> meanings;

    @Schema(description = "Danh sách thẻ", example = "[\"IELTS\", \"Food\", \"A1\"]")
    private List<String> tags;

    @Schema(description = "Ngày tạo", example = "2026-01-27T10:00:00")
    private String createdAt;
}

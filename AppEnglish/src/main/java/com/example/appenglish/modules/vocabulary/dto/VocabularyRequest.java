package com.example.appenglish.modules.vocabulary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * DTO cho Vocabulary (Request - tạo/cập nhật).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "VocabularyRequest", description = "Request body tạo/cập nhật từ vựng")
public class VocabularyRequest {

    @NotBlank(message = "Từ vựng không được để trống")
    @Schema(description = "Từ vựng", example = "hello", requiredMode = Schema.RequiredMode.REQUIRED)
    private String word;

    @Schema(description = "Cấp độ khó (EASY, MEDIUM, HARD)", example = "MEDIUM")
private String level;

    @Schema(description = "Phiên âm IPA", example = "həˈloʊ")
    private String phonetic;

    @Schema(description = "Link phát âm", example = "https://example.com/audio/hello.mp3")
    private String audioUrl;

    @Schema(description = "Danh sách nghĩa", example = "[{\"pos\": \"noun\", \"def\": \"...\", \"ex\": \"...\"}]")
    private List<Map<String, String>> meanings;

    @Schema(description = "Danh sách thẻ", example = "[\"IELTS\", \"Food\", \"A1\"]")
    private List<String> tags;
}

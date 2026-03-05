package com.example.AppEnglish.modules.pronunciation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PronunciationRequest {
    @NotNull
    private Long lessonId;

    @NotNull
    private Integer sentenceIndex;
}


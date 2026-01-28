package com.example.appenglish.modules.transcribe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranscriptSentence {
    private String text;
    private Integer startMs;
    private Integer endMs;
}


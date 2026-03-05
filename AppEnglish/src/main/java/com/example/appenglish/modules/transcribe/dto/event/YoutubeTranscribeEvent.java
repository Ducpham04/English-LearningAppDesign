package com.example.AppEnglish.modules.transcribe.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YoutubeTranscribeEvent {
    private UUID transactionId;
    private Long lessonId;
    private String youtubeUrl;
}

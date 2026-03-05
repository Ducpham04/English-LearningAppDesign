package com.example.AppEnglish.modules.pronunciation.dto.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class PronunciationAudioFinalizeEvent {
    Long attemptId;
    String stagingKey;
    String finalKey;
    String contentType;
}


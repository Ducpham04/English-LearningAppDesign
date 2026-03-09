package com.example.appenglish.modules.ai.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AiChatRequest {

    @NotEmpty(message = "messages is required")
    @Valid
    private List<ChatMessageDto> messages;

    /** Optional context, e.g. lesson title for conversation practice */
    private String lessonContext;

    @Data
    public static class ChatMessageDto {
        private String role; // "system", "user", "assistant"
        private String content;
    }
}

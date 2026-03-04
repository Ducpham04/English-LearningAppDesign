package com.example.appenglish.modules.notification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    @NotBlank(message = "Message is required")
    private String message;

    private Long userId;
    private Map<String, Object> metadata;
}

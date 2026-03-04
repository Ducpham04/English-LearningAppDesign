package com.example.appenglish.modules.notification.dto;

import lombok.*;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long id;
    private Long userId;
    private String message;
    private Boolean isRead;
    private Map<String, Object> metadata;
}

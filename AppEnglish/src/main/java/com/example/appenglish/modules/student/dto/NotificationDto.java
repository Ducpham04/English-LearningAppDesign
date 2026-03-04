package com.example.appenglish.modules.student.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long id;
    private String message;
    private Boolean isRead;
    private String type;
    private String createdAt;
}

package com.example.appenglish.modules.teacher.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ClassJoinRequestDto {
    private Long id;
    private String studentId;
    private String studentName;
    private String studentEmail;
    private String status;
    private LocalDateTime requestedAt;
    private LocalDateTime respondedAt;
}

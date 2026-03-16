package com.example.appenglish.modules.teacher.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ClassStudentDto {
    private String studentId;
    private String fullName;
    private String email;
    private String avatarUrl;
    private LocalDateTime joinedAt;
    private Boolean isFaceVerified;
}

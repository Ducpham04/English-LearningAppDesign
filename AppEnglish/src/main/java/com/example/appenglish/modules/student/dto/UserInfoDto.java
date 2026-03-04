package com.example.appenglish.modules.student.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {
    private String id;
    private String email;
    private String fullName;
    private String avatarUrl;
    private Integer level;
    private Integer xp;
    private Integer streak;
    private Integer totalLessonsCompleted;
    private Boolean faceIdEnrolled;
}

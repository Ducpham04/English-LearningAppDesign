package com.example.appenglish.modules.userDetail.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDataDto {
    private String id;
    private String name;
    private String email;
    private String avatar;
    private String role;
    private Integer level;
    private Integer xp;
    private Integer streak;
    private Integer totalLessonsCompleted;
    private List<String> enrolledClasses;
    private List<String> badges;
    private List<AchievementDto> achievements;
    private Boolean faceIdEnrolled;
    private SettingsDto settings;
    private LocalDateTime createdAt;
    private LocalDateTime lastActiveAt;
}

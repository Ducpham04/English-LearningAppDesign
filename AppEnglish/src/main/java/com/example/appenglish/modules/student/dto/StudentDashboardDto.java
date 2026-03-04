package com.example.appenglish.modules.student.dto;

import lombok.*;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDashboardDto {
    private UserInfoDto user;
    private List<ClassInfoDto> enrolledClasses;
    private List<CourseInfoDto> courses;
    private List<NotificationDto> notifications;
    private List<BadgeDto> badges;
    private DashboardStats stats;
}

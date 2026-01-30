package com.example.appenglish.modules.auth.service;

import com.example.appenglish.entity.User;
import com.example.appenglish.modules.userDetail.dto.UserDataDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserConverterService {

    public UserDataDto convertUserToDataDto(User user) {
        return UserDataDto.builder()
                .id(String.valueOf(user.getId()))
                .name(user.getFullName())
                .email(user.getEmail())
                .avatar(user.getAvatarUrl())
                .role(user.getRole() != null ? user.getRole().getName() : "student")
                .level(5) // TODO: Get from UserProfile or Achievement
                .xp(1250) // TODO: Get from UserProfile
                .streak(7) // TODO: Get from streak logic
                .totalLessonsCompleted(12) // TODO: Get from lesson completion count
                .enrolledClasses(new ArrayList<>()) // TODO: Get from ClassStudent
                .badges(new ArrayList<>()) // TODO: Get from UserBadge
                .achievements(new ArrayList<>()) // TODO: Get from achievements
                .faceIdEnrolled(false) // TODO: Get from FaceVerificationLog
                .settings(null) // TODO: Get from UserProfile settings
                .createdAt(user.getCreatedAt())
                .lastActiveAt(user.getUpdatedAt())
                .build();
    }
}

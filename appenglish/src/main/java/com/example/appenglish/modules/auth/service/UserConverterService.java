package com.example.appenglish.modules.auth.service;

import com.example.appenglish.entity.User;
import com.example.appenglish.modules.userDetail.dto.UserDataDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserConverterService {

    public UserDataDto convertUserToDataDto(User user) {
        return UserDataDto.builder()
                .id(user.getId())
                .name(user.getFullName())
                .email(user.getEmail())
                .avatar(user.getAvatarUrl())
                .role(user.getRole() != null ? user.getRole().name().toLowerCase() : "student")
                .level(user.getLevel() != null ? user.getLevel() : 1)
                .xp(user.getXp() != null ? user.getXp() : 0)
                .streak(user.getStreak() != null ? user.getStreak() : 0)
                .totalLessonsCompleted(user.getTotalLessonsCompleted() != null ? user.getTotalLessonsCompleted() : 0)
                .enrolledClasses(new ArrayList<>())
                .badges(new ArrayList<>())
                .achievements(new ArrayList<>())
                .faceIdEnrolled(user.getFaceIdEnrolled() != null ? user.getFaceIdEnrolled() : false)
                .settings(null)
                .createdAt(user.getCreatedAt())
                .lastActiveAt(user.getUpdatedAt())
                .build();
    }
}

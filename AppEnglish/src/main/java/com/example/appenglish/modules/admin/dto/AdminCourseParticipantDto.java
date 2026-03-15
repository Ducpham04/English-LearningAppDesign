package com.example.appenglish.modules.admin.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class AdminCourseParticipantDto {

    Long userId;
    String fullName;
    String email;
    String avatarUrl;

    Long courseId;
    String courseTitle;

    int completedLessons;
    int totalLessons;
    boolean courseCompleted;

    Instant lastActivityAt;
}


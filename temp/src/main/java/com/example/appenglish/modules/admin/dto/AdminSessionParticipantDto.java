package com.example.appenglish.modules.admin.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class AdminSessionParticipantDto {

    Long userId;
    String fullName;
    String email;
    String avatarUrl;

    Long sessionId;
    String sessionTitle;

    int completedLessons;
    int totalLessons;
    boolean sessionCompleted;

    Instant lastActivityAt;
}


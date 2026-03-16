package com.example.appenglish.modules.admin.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class AdminTopicParticipantDto {

    Long userId;
    String fullName;
    String email;
    String avatarUrl;

    Long topicId;
    String topicName;

    int completedLessons;
    int totalLessons;
    boolean topicCompleted;

    Instant lastActivityAt;
}


package com.example.appenglish.modules.topic.dto;

import lombok.Data;

@Data
public class TopicCreateRequest {
    private String name;
    private String description;
    private String difficultyLevel;
    private Integer orderIndex;
    private Integer estimatedDurationMinutes;
    private String thumbnailUrl;
}

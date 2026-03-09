package com.example.appenglish.modules.topic.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TopicDto {
    private Long id;
    private String name;
    private String description;
    private String difficultyLevel;
    private Integer orderIndex;
    private Integer estimatedDurationMinutes;
    private String thumbnailUrl;
    private Boolean isActive;
    private int sessionCount;
    private List<TopicSessionDto> sessions;

    @Data
    @Builder
    public static class TopicSessionDto {
        private Long id;
        private String title;
        private Integer orderIndex;
        private int lessonCount;
    }
}

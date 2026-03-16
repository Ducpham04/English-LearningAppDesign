package com.example.appenglish.modules.teacher.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignmentUpdateRequest {
    private String title;
    private String description;
    private LocalDateTime dueAt;
    private Integer maxScore;
    private Boolean isRequired;
    private Integer orderIndex;
}

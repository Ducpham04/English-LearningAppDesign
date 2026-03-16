package com.example.appenglish.modules.session.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SessionDto {
    private Long id;
    private String title;
    private Integer orderIndex;
    private Long courseId;
    private String courseTitle;
    private int lessonCount;
}

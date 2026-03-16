package com.example.appenglish.modules.curriculum.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CurriculumDto {
    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private Integer price;
    private String teacherId;
    private String teacherName;
}

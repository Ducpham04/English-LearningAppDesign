package com.example.appenglish.modules.curriculum.dto;

import lombok.Data;

@Data
public class CurriculumCreateRequest {
    private String title;
    private String description;
    private String thumbnailUrl;
    private Integer price;
    private String teacherId;
}

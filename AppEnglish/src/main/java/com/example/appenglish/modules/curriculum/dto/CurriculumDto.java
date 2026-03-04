package com.example.appenglish.modules.curriculum.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurriculumDto {
    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private Integer price;
    private Long teacherId;
    private String teacherName;
}

package com.example.appenglish.modules.test.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestDto {
    private Long id;
    private String title;
    private Integer duration;
    private Integer passScore;
    private Boolean isFaceRequired;
    private Long courseId;
    private String courseName;
    private Long lessonId;
    private String lessonName;
}

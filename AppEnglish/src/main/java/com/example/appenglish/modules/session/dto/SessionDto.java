package com.example.appenglish.modules.session.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionDto {
    private Long id;
    private String title;
    private Integer orderIndex;
    private Long courseId;
    private String courseName;
    private Integer lessonCount;
}

package com.example.appenglish.modules.student.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseInfoDto {
    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private Integer price;
    private String teacherName;
    private Integer totalSessions;
    private Integer completedSessions;
    private Integer progressPercent;
}

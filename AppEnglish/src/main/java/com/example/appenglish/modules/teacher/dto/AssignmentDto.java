package com.example.appenglish.modules.teacher.dto;

import com.example.appenglish.modules.lesson.dto.LessonDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AssignmentDto {
    private Long id;
    private Long classId;
    private Long lessonId;
    private String title;
    private String description;
    private LocalDateTime dueAt;
    private Integer maxScore;
    private Boolean isRequired;
    private Integer orderIndex;
    private LessonDto lesson; // optional, for detail view
}

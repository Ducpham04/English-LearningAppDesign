package com.example.appenglish.modules.teacher.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AssignmentStatsDto {
    private Long assignmentId;
    private String title;
    private Long lessonId;
    private LocalDateTime dueAt;
    private int totalStudents;
    private int completedCount;
    private List<StudentProgressItemDto> students;

    @Data
    @Builder
    public static class StudentProgressItemDto {
        private String studentId;
        private String studentName;
        private String email;
        private Boolean completed;
        private Integer score;
        private LocalDateTime completedAt;
    }
}

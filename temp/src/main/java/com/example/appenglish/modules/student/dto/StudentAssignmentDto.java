package com.example.appenglish.modules.student.dto;

import com.example.appenglish.modules.lesson.dto.LessonDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/** Bài tập trong lớp dành cho học viên: kèm trạng thái hoàn thành và điểm. */
@Data
@Builder
public class StudentAssignmentDto {
    private Long id;
    private Long classId;
    private Long lessonId;
    private String title;
    private String description;
    private LocalDateTime dueAt;
    private Integer maxScore;
    private Boolean isRequired;
    private Integer orderIndex;
    private LessonDto lesson;

    /** Học viên đã hoàn thành bài này chưa. */
    private Boolean completed;
    /** Điểm đạt được (nếu đã hoàn thành). */
    private Integer score;
    /** Loại bài: VIDEO, QUIZ, ... từ lesson.type. */
    private String lessonType;
}

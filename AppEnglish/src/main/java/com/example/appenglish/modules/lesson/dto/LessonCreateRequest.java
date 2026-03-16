package com.example.appenglish.modules.lesson.dto;

import lombok.Data;

@Data
public class LessonCreateRequest {
    private String title;
    private String description;
    private String type;          // VIDEO, QUIZ, VOCAB, SPEAKING
    private Integer orderIndex;
    private Integer durationMinutes;
    private Long sessionId;
    private Long courseId;
}

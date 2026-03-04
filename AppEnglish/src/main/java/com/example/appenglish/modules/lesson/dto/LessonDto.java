package com.example.appenglish.modules.lesson.dto;

import com.example.appenglish.enums.LessonType;
import lombok.*;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonDto {
    private Long id;
    private String title;
    private Long courseId;
    private String courseName;
    private Long sessionId;
    private String sessionName;
    private LessonType type;
    private Integer orderIndex;
    private Map<String, Object> contentPayload;
}

package com.example.appenglish.modules.lesson.dto;

import com.example.appenglish.enums.LessonType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private Long courseId;
    private Long sessionId;

    @NotNull(message = "Lesson type is required")
    private LessonType type;

    private Integer orderIndex;
    private Map<String, Object> contentPayload;
}

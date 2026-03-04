package com.example.appenglish.modules.test.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private Integer duration;
    private Integer passScore;
    private Boolean isFaceRequired;
    private Long courseId;
    private Long lessonId;
}

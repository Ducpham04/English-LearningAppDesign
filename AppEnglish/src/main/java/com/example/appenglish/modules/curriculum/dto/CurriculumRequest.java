package com.example.appenglish.modules.curriculum.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurriculumRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;
    private String thumbnailUrl;
    private Integer price;
    private Long teacherId;
}

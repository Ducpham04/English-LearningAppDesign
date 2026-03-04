package com.example.appenglish.modules.session.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private Integer orderIndex;

    @NotNull(message = "Course ID is required")
    private Long courseId;
}

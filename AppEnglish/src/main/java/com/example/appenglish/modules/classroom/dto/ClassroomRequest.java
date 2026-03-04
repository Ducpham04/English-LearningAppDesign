package com.example.appenglish.modules.classroom.dto;

import com.example.appenglish.enums.ClassStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomRequest {
    @NotBlank(message = "Class name is required")
    private String name;

    @NotNull(message = "Teacher ID is required")
    private Long teacherId;

    private LocalDate startDate;
    private LocalDate endDate;
    private ClassStatus status;
}

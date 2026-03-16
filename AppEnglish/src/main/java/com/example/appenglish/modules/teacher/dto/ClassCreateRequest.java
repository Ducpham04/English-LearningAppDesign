package com.example.appenglish.modules.teacher.dto;

import com.example.appenglish.enums.ClassStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ClassCreateRequest {
    private String name;
    private Long courseId;
    private LocalDate startDate;
    private LocalDate endDate;
    private ClassStatus status; // optional, default UPCOMING
}

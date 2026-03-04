package com.example.appenglish.modules.classroom.dto;

import com.example.appenglish.enums.ClassStatus;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomDto {
    private Long id;
    private String name;
    private Long teacherId;
    private String teacherName;
    private LocalDate startDate;
    private LocalDate endDate;
    private ClassStatus status;
    private Integer studentCount;
}

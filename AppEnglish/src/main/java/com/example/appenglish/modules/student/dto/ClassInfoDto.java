package com.example.appenglish.modules.student.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassInfoDto {
    private Long id;
    private String name;
    private String teacherName;
    private String teacherAvatar;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Integer studentCount;
}

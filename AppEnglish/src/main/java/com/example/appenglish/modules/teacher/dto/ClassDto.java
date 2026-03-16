package com.example.appenglish.modules.teacher.dto;

import com.example.appenglish.enums.ClassStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ClassDto {
    private Long id;
    private String name;
    /** Mã lớp để học viên nhập khi xin vào lớp. */
    private String joinCode;
    private String teacherId;
    private String teacherName;
    private Long courseId;
    private String courseTitle;
    private LocalDate startDate;
    private LocalDate endDate;
    private ClassStatus status;
    private Integer studentCount;
    private Integer assignmentCount;
}

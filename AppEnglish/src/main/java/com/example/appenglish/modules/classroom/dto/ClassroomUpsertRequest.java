package com.example.appenglish.modules.classroom.dto;

import com.example.appenglish.enums.ClassStatus;
import java.time.LocalDate;
import lombok.Data;

@Data
public class ClassroomUpsertRequest {
  private String name;
  private String teacherId;
  private Long courseId;
  private LocalDate startDate;
  private LocalDate endDate;
  private ClassStatus status;
}


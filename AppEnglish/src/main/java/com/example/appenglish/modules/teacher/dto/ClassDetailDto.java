package com.example.appenglish.modules.teacher.dto;

import com.example.appenglish.modules.curriculum.dto.CurriculumDto;
import com.example.appenglish.modules.lesson.dto.LessonDto;
import com.example.appenglish.modules.session.dto.SessionDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ClassDetailDto {
    private ClassDto classInfo;
    private CurriculumDto course;
    private List<SessionDto> sessions;
    private List<LessonDto> lessons; // all lessons in course (by session order)
    private List<ClassStudentDto> students;
    private List<AssignmentDto> assignments;
}

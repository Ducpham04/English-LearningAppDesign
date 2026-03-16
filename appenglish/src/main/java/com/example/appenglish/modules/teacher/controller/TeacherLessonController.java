package com.example.appenglish.modules.teacher.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.modules.lesson.dto.LessonDto;
import com.example.appenglish.modules.lesson.service.LessonService;
import com.example.appenglish.modules.teacher.service.TeacherClassService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * API for teacher to list lessons they can assign in a class (from their own curricula).
 */
@RestController
@RequestMapping("/v1/teacher/lessons")
@RequiredArgsConstructor
public class TeacherLessonController {

    private final TeacherClassService teacherClassService;
    private final LessonService lessonService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<LessonDto>>> getMyLessons(HttpServletRequest req) {
        try {
            var teacher = teacherClassService.getCurrentTeacher();
            List<LessonDto> lessons = lessonService.getByTeacher(teacher.getId());
            return ResponseEntity.ok(BaseResponse.ok(lessons, "OK", req.getRequestURI()));
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(BaseResponse.error(e.getMessage(), "NOT_FOUND", req.getRequestURI()));
            }
            return ResponseEntity.status(403).body(BaseResponse.error(e.getMessage(), "FORBIDDEN", req.getRequestURI()));
        }
    }
}

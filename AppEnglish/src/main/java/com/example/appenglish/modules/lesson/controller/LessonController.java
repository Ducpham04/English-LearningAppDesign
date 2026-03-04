package com.example.appenglish.modules.lesson.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.modules.lesson.dto.LessonDto;
import com.example.appenglish.modules.lesson.dto.LessonRequest;
import com.example.appenglish.modules.lesson.service.LessonService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<LessonDto>>> getAllLessons(HttpServletRequest httpReq) {
        List<LessonDto> lessons = lessonService.getAllLessons();
        return ResponseEntity.ok(
                BaseResponse.ok(lessons, "Get all lessons successfully", httpReq.getRequestURI())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<LessonDto>> getLessonById(
            @PathVariable Long id,
            HttpServletRequest httpReq) {
        LessonDto lesson = lessonService.getLessonById(id);
        return ResponseEntity.ok(
                BaseResponse.ok(lesson, "Get lesson successfully", httpReq.getRequestURI())
        );
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<BaseResponse<List<LessonDto>>> getLessonsByCourse(
            @PathVariable Long courseId,
            HttpServletRequest httpReq) {
        List<LessonDto> lessons = lessonService.getLessonsByCourse(courseId);
        return ResponseEntity.ok(
                BaseResponse.ok(lessons, "Get lessons by course successfully", httpReq.getRequestURI())
        );
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<BaseResponse<List<LessonDto>>> getLessonsBySession(
            @PathVariable Long sessionId,
            HttpServletRequest httpReq) {
        List<LessonDto> lessons = lessonService.getLessonsBySession(sessionId);
        return ResponseEntity.ok(
                BaseResponse.ok(lessons, "Get lessons by session successfully", httpReq.getRequestURI())
        );
    }

    @PostMapping
    public ResponseEntity<BaseResponse<LessonDto>> createLesson(
            @Valid @RequestBody LessonRequest request,
            HttpServletRequest httpReq) {
        LessonDto lesson = lessonService.createLesson(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.ok(lesson, "Lesson created successfully", httpReq.getRequestURI()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<LessonDto>> updateLesson(
            @PathVariable Long id,
            @Valid @RequestBody LessonRequest request,
            HttpServletRequest httpReq) {
        LessonDto lesson = lessonService.updateLesson(id, request);
        return ResponseEntity.ok(
                BaseResponse.ok(lesson, "Lesson updated successfully", httpReq.getRequestURI())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<?>> deleteLesson(
            @PathVariable Long id,
            HttpServletRequest httpReq) {
        lessonService.deleteLesson(id);
        return ResponseEntity.ok(
                BaseResponse.ok(null, "Lesson deleted successfully", httpReq.getRequestURI())
        );
    }
}

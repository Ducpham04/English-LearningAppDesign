package com.example.appenglish.modules.student.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.modules.student.dto.LessonCompleteRequest;
import com.example.appenglish.modules.student.dto.LessonCompleteResponse;
import com.example.appenglish.modules.student.dto.StudentDashboardDto;
import com.example.appenglish.modules.student.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/student")
@RequiredArgsConstructor
@Slf4j
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/dashboard")
    public ResponseEntity<BaseResponse<StudentDashboardDto>> getDashboard(HttpServletRequest httpReq) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(401)
                        .body(BaseResponse.error("Unauthorized", "UNAUTHORIZED", httpReq.getRequestURI()));
            }
            
            String email = auth.getName();
            StudentDashboardDto dashboard = studentService.getStudentDashboardByEmail(email);
            
            return ResponseEntity.ok(
                    BaseResponse.ok(dashboard, "Get student dashboard successfully", httpReq.getRequestURI())
            );
        } catch (Exception e) {
            log.error("Failed to fetch student dashboard", e);
            return ResponseEntity.status(500)
                    .body(BaseResponse.error("Failed to fetch student dashboard", "DASHBOARD_FETCH_FAILED", httpReq.getRequestURI()));
        }
    }

    @GetMapping("/courses")
    public ResponseEntity<BaseResponse<?>> getCourses(HttpServletRequest httpReq) {
        try {
            var courses = studentService.getAllCourses();
            return ResponseEntity.ok(
                    BaseResponse.ok(courses, "Get courses successfully", httpReq.getRequestURI())
            );
        } catch (Exception e) {
            log.error("Failed to fetch courses", e);
            return ResponseEntity.status(500)
                    .body(BaseResponse.error("Failed to fetch courses", "COURSES_FETCH_FAILED", httpReq.getRequestURI()));
        }
    }

    @PostMapping("/lessons/{lessonId}/complete")
    public ResponseEntity<BaseResponse<LessonCompleteResponse>> completeLesson(
            @PathVariable Long lessonId,
            @RequestBody(required = false) LessonCompleteRequest body,
            HttpServletRequest httpReq) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(401)
                        .body(BaseResponse.error("Unauthorized", "UNAUTHORIZED", httpReq.getRequestURI()));
            }
            String email = auth.getName();
            Integer score = body != null ? body.getScore() : null;
            LessonCompleteResponse result = studentService.completeLesson(email, lessonId, score);
            return ResponseEntity.ok(
                    BaseResponse.ok(result, "Lesson completed successfully", httpReq.getRequestURI())
            );
        } catch (RuntimeException e) {
            log.error("Failed to complete lesson", e);
            return ResponseEntity.status(400)
                    .body(BaseResponse.error(e.getMessage(), "COMPLETE_LESSON_FAILED", httpReq.getRequestURI()));
        } catch (Exception e) {
            log.error("Failed to complete lesson", e);
            return ResponseEntity.status(500)
                    .body(BaseResponse.error("Failed to save progress", "COMPLETE_LESSON_FAILED", httpReq.getRequestURI()));
        }
    }
}

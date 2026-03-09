package com.example.appenglish.modules.student.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.modules.curriculum.dto.CurriculumDto;
import com.example.appenglish.modules.student.dto.SessionProgressDto;
import com.example.appenglish.modules.student.dto.StudentDashboardDto;
import com.example.appenglish.modules.student.service.StudentService;
import com.example.appenglish.modules.userDetail.dto.UserDataDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/dashboard")
    public ResponseEntity<BaseResponse<StudentDashboardDto>> getDashboard(HttpServletRequest req) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(studentService.getDashboard(), "OK", req.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(BaseResponse.error(e.getMessage(), "UNAUTHORIZED", req.getRequestURI()));
        }
    }

    @GetMapping("/courses")
    public ResponseEntity<BaseResponse<List<CurriculumDto>>> getCourses(HttpServletRequest req) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(studentService.getCourses(), "OK", req.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(BaseResponse.error(e.getMessage(), "ERROR", req.getRequestURI()));
        }
    }

    @PostMapping("/lessons/{lessonId}/complete")
    public ResponseEntity<BaseResponse<UserDataDto>> completeLesson(
            @PathVariable Long lessonId,
            @RequestBody(required = false) Map<String, Integer> body,
            HttpServletRequest req) {
        try {
            Integer score = body != null ? body.get("score") : null;
            UserDataDto updated = studentService.completLesson(lessonId, score);
            return ResponseEntity.ok(BaseResponse.ok(updated, "Lesson completed", req.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(BaseResponse.error(e.getMessage(), "UNAUTHORIZED", req.getRequestURI()));
        }
    }

    @GetMapping("/progress/completed-lesson-ids")
    public ResponseEntity<BaseResponse<List<Long>>> getCompletedLessonIds(HttpServletRequest req) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(studentService.getCompletedLessonIds(), "OK", req.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(BaseResponse.error(e.getMessage(), "UNAUTHORIZED", req.getRequestURI()));
        }
    }

    @GetMapping("/progress/completed-session-ids")
    public ResponseEntity<BaseResponse<List<Long>>> getCompletedSessionIds(HttpServletRequest req) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(studentService.getCompletedSessionIds(), "OK", req.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(BaseResponse.error(e.getMessage(), "UNAUTHORIZED", req.getRequestURI()));
        }
    }

    @GetMapping("/progress/participated-sessions")
    public ResponseEntity<BaseResponse<List<SessionProgressDto>>> getParticipatedSessions(HttpServletRequest req) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(studentService.getParticipatedSessions(), "OK", req.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(BaseResponse.error(e.getMessage(), "UNAUTHORIZED", req.getRequestURI()));
        }
    }
}

package com.example.appenglish.modules.student.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.modules.curriculum.dto.CurriculumDto;
import com.example.appenglish.modules.student.dto.SessionProgressDto;
import com.example.appenglish.modules.student.dto.StudentDashboardDto;
import com.example.appenglish.modules.student.service.StudentService;
import com.example.appenglish.modules.userDetail.dto.UserDataDto;
import com.example.appenglish.modules.vocabulary.dto.AddToMyVocabularyRequest;
import com.example.appenglish.modules.vocabulary.dto.AddToMyVocabularyResponseDto;
import com.example.appenglish.modules.vocabulary.dto.MyVocabularyItemDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

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

    @GetMapping("/classes")
    public ResponseEntity<BaseResponse<List<StudentDashboardDto.EnrolledClassDto>>> getMyClasses(HttpServletRequest req) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(studentService.getEnrolledClasses(), "OK", req.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(BaseResponse.error(e.getMessage(), "UNAUTHORIZED", req.getRequestURI()));
        }
    }

    @PostMapping("/classes/join-request")
    public ResponseEntity<BaseResponse<Map<String, String>>> requestToJoinClass(
            @RequestBody Map<String, String> body,
            HttpServletRequest req) {
        try {
            String joinCode = body != null ? body.get("joinCode") : null;
            String message = studentService.requestToJoinByCode(joinCode);
            return ResponseEntity.ok(BaseResponse.ok(new HashMap<>(Map.of("message", message)), message, req.getRequestURI()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(BaseResponse.error(e.getMessage(), "BAD_REQUEST", req.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(BaseResponse.error(e.getMessage(), "UNAUTHORIZED", req.getRequestURI()));
        }
    }

    @GetMapping("/classes/{classId}/assignments")
    public ResponseEntity<BaseResponse<List<com.example.appenglish.modules.student.dto.StudentAssignmentDto>>> getClassAssignments(
            @PathVariable Long classId,
            HttpServletRequest req) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(studentService.getClassAssignments(classId), "OK", req.getRequestURI()));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Not enrolled")) {
                return ResponseEntity.status(403).body(BaseResponse.error(e.getMessage(), "FORBIDDEN", req.getRequestURI()));
            }
            return ResponseEntity.status(404).body(BaseResponse.error(e.getMessage(), "NOT_FOUND", req.getRequestURI()));
        }
    }

    @PostMapping("/my-vocabulary")
    public ResponseEntity<BaseResponse<AddToMyVocabularyResponseDto>> addToMyVocabulary(
            @RequestBody AddToMyVocabularyRequest body,
            HttpServletRequest req) {
        try {
            AddToMyVocabularyResponseDto result = studentService.addToMyVocabulary(body);
            return ResponseEntity.ok(BaseResponse.ok(result, "Đã lưu từ vựng", req.getRequestURI()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(BaseResponse.error(e.getMessage(), "BAD_REQUEST", req.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(BaseResponse.error(e.getMessage(), "UNAUTHORIZED", req.getRequestURI()));
        }
    }

    @GetMapping("/my-vocabulary")
    public ResponseEntity<BaseResponse<List<MyVocabularyItemDto>>> getMyVocabulary(
            @RequestParam(required = false) Long lessonId,
            @RequestParam(required = false) Long sessionId,
            HttpServletRequest req) {
        try {
            List<MyVocabularyItemDto> list = studentService.getMyVocabulary(lessonId, sessionId);
            return ResponseEntity.ok(BaseResponse.ok(list, "OK", req.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(BaseResponse.error(e.getMessage(), "UNAUTHORIZED", req.getRequestURI()));
        }
    }

    @DeleteMapping("/my-vocabulary/{vocabularyId}")
    public ResponseEntity<BaseResponse<Void>> removeFromMyVocabulary(
            @PathVariable Long vocabularyId,
            HttpServletRequest req) {
        try {
            studentService.removeFromMyVocabulary(vocabularyId);
            return ResponseEntity.ok(BaseResponse.ok(null, "Đã xóa từ khỏi danh sách", req.getRequestURI()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(BaseResponse.error(e.getMessage(), "BAD_REQUEST", req.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(BaseResponse.error(e.getMessage(), "UNAUTHORIZED", req.getRequestURI()));
        }
    }
}

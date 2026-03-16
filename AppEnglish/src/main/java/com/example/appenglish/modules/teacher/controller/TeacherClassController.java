package com.example.appenglish.modules.teacher.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.modules.teacher.dto.*;
import com.example.appenglish.modules.teacher.service.TeacherClassService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/teacher/classes")
@RequiredArgsConstructor
public class TeacherClassController {

    private final TeacherClassService teacherClassService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<ClassDto>>> getMyClasses(HttpServletRequest req) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(teacherClassService.getMyClasses(), "OK", req.getRequestURI()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(BaseResponse.error(e.getMessage(), "FORBIDDEN", req.getRequestURI()));
        }
    }

    @PostMapping
    public ResponseEntity<BaseResponse<ClassDto>> createClass(@RequestBody ClassCreateRequest body, HttpServletRequest req) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(teacherClassService.createClass(body), "Class created", req.getRequestURI()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(BaseResponse.error(e.getMessage(), "BAD_REQUEST", req.getRequestURI()));
        }
    }

    @GetMapping("/{classId}")
    public ResponseEntity<BaseResponse<ClassDetailDto>> getClassDetail(@PathVariable Long classId, HttpServletRequest req) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(teacherClassService.getClassDetail(classId), "OK", req.getRequestURI()));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(BaseResponse.error(e.getMessage(), "NOT_FOUND", req.getRequestURI()));
            }
            return ResponseEntity.status(403).body(BaseResponse.error(e.getMessage(), "FORBIDDEN", req.getRequestURI()));
        }
    }

    @GetMapping("/{classId}/students")
    public ResponseEntity<BaseResponse<List<ClassStudentDto>>> getStudents(@PathVariable Long classId, HttpServletRequest req) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(teacherClassService.getClassStudents(classId), "OK", req.getRequestURI()));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(BaseResponse.error(e.getMessage(), "NOT_FOUND", req.getRequestURI()));
            }
            return ResponseEntity.status(403).body(BaseResponse.error(e.getMessage(), "FORBIDDEN", req.getRequestURI()));
        }
    }

    @PostMapping("/{classId}/students")
    public ResponseEntity<BaseResponse<ClassStudentDto>> addStudent(
            @PathVariable Long classId,
            @RequestBody AddStudentRequest body,
            HttpServletRequest req) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(
                    teacherClassService.addStudent(classId, body.getStudentId()), "Student added", req.getRequestURI()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(BaseResponse.error(e.getMessage(), "BAD_REQUEST", req.getRequestURI()));
        }
    }

    @DeleteMapping("/{classId}/students/{studentId}")
    public ResponseEntity<BaseResponse<Void>> removeStudent(
            @PathVariable Long classId,
            @PathVariable String studentId,
            HttpServletRequest req) {
        try {
            teacherClassService.removeStudent(classId, studentId);
            return ResponseEntity.ok(BaseResponse.ok(null, "Student removed", req.getRequestURI()));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(BaseResponse.error(e.getMessage(), "NOT_FOUND", req.getRequestURI()));
            }
            return ResponseEntity.status(403).body(BaseResponse.error(e.getMessage(), "FORBIDDEN", req.getRequestURI()));
        }
    }

    @DeleteMapping("/{classId}")
    public ResponseEntity<BaseResponse<Void>> deleteClass(@PathVariable Long classId, HttpServletRequest req) {
        try {
            teacherClassService.deleteClass(classId);
            return ResponseEntity.ok(BaseResponse.ok(null, "Class deleted", req.getRequestURI()));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(BaseResponse.error(e.getMessage(), "NOT_FOUND", req.getRequestURI()));
            }
            return ResponseEntity.status(403).body(BaseResponse.error(e.getMessage(), "FORBIDDEN", req.getRequestURI()));
        }
    }

    @GetMapping("/{classId}/assignments")
    public ResponseEntity<BaseResponse<List<AssignmentDto>>> getAssignments(@PathVariable Long classId, HttpServletRequest req) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(teacherClassService.getAssignments(classId), "OK", req.getRequestURI()));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(BaseResponse.error(e.getMessage(), "NOT_FOUND", req.getRequestURI()));
            }
            return ResponseEntity.status(403).body(BaseResponse.error(e.getMessage(), "FORBIDDEN", req.getRequestURI()));
        }
    }

    @GetMapping("/{classId}/assignment-stats")
    public ResponseEntity<BaseResponse<List<AssignmentStatsDto>>> getAssignmentStats(@PathVariable Long classId, HttpServletRequest req) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(teacherClassService.getAssignmentStats(classId), "OK", req.getRequestURI()));
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(BaseResponse.error(e.getMessage(), "NOT_FOUND", req.getRequestURI()));
            }
            return ResponseEntity.status(403).body(BaseResponse.error(e.getMessage(), "FORBIDDEN", req.getRequestURI()));
        }
    }

    @PostMapping("/{classId}/assignments")
    public ResponseEntity<BaseResponse<AssignmentDto>> createAssignment(
            @PathVariable Long classId,
            @RequestBody AssignmentCreateRequest body,
            HttpServletRequest req) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(
                    teacherClassService.createAssignment(classId, body), "Assignment created", req.getRequestURI()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(BaseResponse.error(e.getMessage(), "BAD_REQUEST", req.getRequestURI()));
        }
    }

    @PutMapping("/{classId}/assignments/{assignmentId}")
    public ResponseEntity<BaseResponse<AssignmentDto>> updateAssignment(
            @PathVariable Long classId,
            @PathVariable Long assignmentId,
            @RequestBody AssignmentUpdateRequest body,
            HttpServletRequest req) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(
                    teacherClassService.updateAssignment(classId, assignmentId, body), "Assignment updated", req.getRequestURI()));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(BaseResponse.error(e.getMessage(), "NOT_FOUND", req.getRequestURI()));
            }
            return ResponseEntity.status(400).body(BaseResponse.error(e.getMessage(), "BAD_REQUEST", req.getRequestURI()));
        }
    }

    @DeleteMapping("/{classId}/assignments/{assignmentId}")
    public ResponseEntity<BaseResponse<Void>> deleteAssignment(
            @PathVariable Long classId,
            @PathVariable Long assignmentId,
            HttpServletRequest req) {
        try {
            teacherClassService.deleteAssignment(classId, assignmentId);
            return ResponseEntity.ok(BaseResponse.ok(null, "Assignment deleted", req.getRequestURI()));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(BaseResponse.error(e.getMessage(), "NOT_FOUND", req.getRequestURI()));
            }
            return ResponseEntity.status(403).body(BaseResponse.error(e.getMessage(), "FORBIDDEN", req.getRequestURI()));
        }
    }

    @GetMapping("/{classId}/join-requests")
    public ResponseEntity<BaseResponse<List<ClassJoinRequestDto>>> getJoinRequests(
            @PathVariable Long classId,
            HttpServletRequest req) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(teacherClassService.getJoinRequests(classId), "OK", req.getRequestURI()));
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(BaseResponse.error(e.getMessage(), "NOT_FOUND", req.getRequestURI()));
            }
            return ResponseEntity.status(403).body(BaseResponse.error(e.getMessage(), "FORBIDDEN", req.getRequestURI()));
        }
    }

    @PostMapping("/{classId}/join-requests/{requestId}/approve")
    public ResponseEntity<BaseResponse<Void>> approveJoinRequest(
            @PathVariable Long classId,
            @PathVariable Long requestId,
            HttpServletRequest req) {
        try {
            teacherClassService.approveJoinRequest(classId, requestId);
            return ResponseEntity.ok(BaseResponse.ok(null, "Đã duyệt yêu cầu", req.getRequestURI()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(BaseResponse.error(e.getMessage(), "BAD_REQUEST", req.getRequestURI()));
        }
    }

    @PostMapping("/{classId}/join-requests/{requestId}/reject")
    public ResponseEntity<BaseResponse<Void>> rejectJoinRequest(
            @PathVariable Long classId,
            @PathVariable Long requestId,
            HttpServletRequest req) {
        try {
            teacherClassService.rejectJoinRequest(classId, requestId);
            return ResponseEntity.ok(BaseResponse.ok(null, "Đã từ chối yêu cầu", req.getRequestURI()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(BaseResponse.error(e.getMessage(), "BAD_REQUEST", req.getRequestURI()));
        }
    }

    @lombok.Data
    public static class AddStudentRequest {
        private String studentId;
    }
}

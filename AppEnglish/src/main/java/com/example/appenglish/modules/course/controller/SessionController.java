package com.example.appenglish.modules.course.controller;

import com.example.appenglish.entity.Session;
import com.example.appenglish.modules.course.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller xử lý các yêu cầu liên quan đến Session (Chương học).
 */
@RestController
@RequestMapping("/api/curriculums")
@RequiredArgsConstructor
@Tag(name = "Session")
public class SessionController {

    private final SessionService sessionService;

    /**
     * GET /api/curriculums/{curriculumId}/sessions
     * Lấy danh sách toàn bộ chương học của một khóa học, sắp xếp theo thứ tự (orderIndex).
     * * @param curriculumId ID của khóa học (Curriculum/Course)
     * @return Danh sách các Session thuộc khóa học đó
     */
    @GetMapping("/{curriculumId}/sessions")
   public ResponseEntity<List<Session>> getSessionList(
            @PathVariable 
            @Parameter(description = "ID của khóa học", example = "1") Long curriculumId) {
        
        // Gọi service xử lý logic lấy và sắp xếp dữ liệu
        List<Session> sessions = sessionService.getSessionsByCurriculum(curriculumId);
        
        if (sessions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(sessions);
    }
}
package com.example.appenglish.modules.course.controller;

import com.example.appenglish.modules.session.dto.SessionResponse;
import com.example.appenglish.modules.course.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@Tag(name = "Session", description = "Quản lý chương học và lộ trình học tập")
public class SessionController {

    private final SessionService service;

    /**
     * Wrapper cho API 5.1 (Danh sách Session kèm phân trang)
     * Thay 'Object' bằng 'SessionResponse.SessionListWrapper' để Swagger hiển thị Example Value
     */
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SessionListDataResponse {
        private boolean success;
        private SessionResponse.SessionListWrapper data;
    }

    /**
     * Wrapper cho API 5.2 (Chi tiết Session)
     * Thay 'Object' bằng 'SessionResponse' để Swagger hiển thị chi tiết Lesson và UserProgress
     */
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SessionDetailDataResponse {
        private boolean success;
        private SessionResponse data;
    }

    @GetMapping
    @Operation(summary = "5.1 Get All Sessions", description = "Lấy danh sách các session kèm phân trang, level và trạng thái")
    public ResponseEntity<SessionListDataResponse> getAll(
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        
        var data = service.getAllSessions(level, status, PageRequest.of(page - 1, limit));
        
        return ResponseEntity.ok(SessionListDataResponse.builder()
                .success(true)
                .data(data)
                .build());
    }

    @GetMapping("/{sessionId}")
    @Operation(summary = "5.2 Get Session Details", description = "Lấy chi tiết một session bao gồm danh sách bài học và tiến độ của người dùng")
    public ResponseEntity<SessionDetailDataResponse> getDetails(@PathVariable Long sessionId) {
        // Tạm thời fix cứng userId = 1L để test không cần login
        Long userId = 1L; 
        var data = service.getSessionDetails(sessionId, userId);
        
        return ResponseEntity.ok(SessionDetailDataResponse.builder()
                .success(true)
                .data(data)
                .build());
    }
}
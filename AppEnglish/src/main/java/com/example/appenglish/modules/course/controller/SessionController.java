package com.example.appenglish.modules.course.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.common.utils.SecurityUtils;
import com.example.appenglish.modules.session.dto.SessionResponse;
import com.example.appenglish.modules.course.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
@Tag(name = "Session")
public class SessionController {

    private final SessionService sessionService;

    @GetMapping("/{sessionId}")
    @Operation(summary = "Get session details")
    public ResponseEntity<BaseResponse<SessionResponse>> getSession(
            @PathVariable Long sessionId,
            HttpServletRequest request) {

        Long userId = SecurityUtils.getCurrentUserId();
        SessionResponse data = sessionService.getSessionDetails(sessionId, userId);

        return ResponseEntity.ok(
            BaseResponse.ok(data, "Fetch session details successfully", request.getRequestURI())
        );
    }

    @GetMapping
    @Operation(summary = "Get all sessions")
    public ResponseEntity<BaseResponse<SessionResponse.SessionListWrapper>> getAllSessions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            HttpServletRequest request) {

        Long userId = SecurityUtils.getCurrentUserId();
        SessionResponse.SessionListWrapper data = sessionService.getAllSessions(page, limit, userId);

        return ResponseEntity.ok(
            BaseResponse.ok(data, "Fetch sessions successfully", request.getRequestURI())
        );
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get sessions by course")
    public ResponseEntity<BaseResponse<SessionResponse.SessionListWrapper>> getSessionsByCourse(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            HttpServletRequest request) {

        Long userId = SecurityUtils.getCurrentUserId();
        SessionResponse.SessionListWrapper data = sessionService.getSessionsByCourse(courseId, page, limit, userId);

        return ResponseEntity.ok(
            BaseResponse.ok(data, "Fetch sessions by course successfully", request.getRequestURI())
        );
    }
}
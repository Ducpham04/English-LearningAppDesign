package com.example.AppEnglish.modules.course.controller;

import com.example.AppEnglish.common.dto.BaseResponse;
import com.example.AppEnglish.modules.course.dto.SessionListDTO;
import com.example.AppEnglish.modules.course.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/sessions")
@RequiredArgsConstructor
@Tag(name = "Session", description = "Session management APIs")
public class SessionController {

    private final SessionService sessionService;

    @GetMapping
    @Operation(summary = "Get all sessions", description = "List sessions with optional level, status filter and pagination")
    public ResponseEntity<BaseResponse<Page<SessionListDTO>>> getAllSessions(
            @Parameter(description = "Filter by level: beginner, intermediate, advanced") @RequestParam(required = false) String level,

            @Parameter(description = "Filter by status: locked, in-progress, completed") @RequestParam(required = false) String status,

            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "20") int limit,

            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page, limit, Sort.by("orderIndex").ascending());
        Page<SessionListDTO> sessions = sessionService.getSessionList(level, status, pageable);

        return ResponseEntity.ok(BaseResponse.ok(sessions, "Sessions retrieved", request.getRequestURI()));
    }
}

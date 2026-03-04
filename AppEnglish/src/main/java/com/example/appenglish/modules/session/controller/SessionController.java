package com.example.appenglish.modules.session.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.modules.session.dto.SessionDto;
import com.example.appenglish.modules.session.dto.SessionRequest;
import com.example.appenglish.modules.session.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<SessionDto>>> getAllSessions(HttpServletRequest httpReq) {
        List<SessionDto> sessions = sessionService.getAllSessions();
        return ResponseEntity.ok(
                BaseResponse.ok(sessions, "Get all sessions successfully", httpReq.getRequestURI())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<SessionDto>> getSessionById(
            @PathVariable Long id,
            HttpServletRequest httpReq) {
        SessionDto session = sessionService.getSessionById(id);
        return ResponseEntity.ok(
                BaseResponse.ok(session, "Get session successfully", httpReq.getRequestURI())
        );
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<BaseResponse<List<SessionDto>>> getSessionsByCourse(
            @PathVariable Long courseId,
            HttpServletRequest httpReq) {
        List<SessionDto> sessions = sessionService.getSessionsByCourse(courseId);
        return ResponseEntity.ok(
                BaseResponse.ok(sessions, "Get sessions by course successfully", httpReq.getRequestURI())
        );
    }

    @PostMapping
    public ResponseEntity<BaseResponse<SessionDto>> createSession(
            @Valid @RequestBody SessionRequest request,
            HttpServletRequest httpReq) {
        SessionDto session = sessionService.createSession(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.ok(session, "Session created successfully", httpReq.getRequestURI()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<SessionDto>> updateSession(
            @PathVariable Long id,
            @Valid @RequestBody SessionRequest request,
            HttpServletRequest httpReq) {
        SessionDto session = sessionService.updateSession(id, request);
        return ResponseEntity.ok(
                BaseResponse.ok(session, "Session updated successfully", httpReq.getRequestURI())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<?>> deleteSession(
            @PathVariable Long id,
            HttpServletRequest httpReq) {
        sessionService.deleteSession(id);
        return ResponseEntity.ok(
                BaseResponse.ok(null, "Session deleted successfully", httpReq.getRequestURI())
        );
    }
}

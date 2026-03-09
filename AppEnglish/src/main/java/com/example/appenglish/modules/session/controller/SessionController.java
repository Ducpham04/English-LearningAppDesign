package com.example.appenglish.modules.session.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.modules.session.dto.SessionCreateRequest;
import com.example.appenglish.modules.session.dto.SessionDto;
import com.example.appenglish.modules.session.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<SessionDto>>> getAll(HttpServletRequest req) {
        return ResponseEntity.ok(BaseResponse.ok(sessionService.getAll(), "OK", req.getRequestURI()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<SessionDto>> getById(@PathVariable Long id, HttpServletRequest req) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(sessionService.getById(id), "OK", req.getRequestURI()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(BaseResponse.error(e.getMessage(), "NOT_FOUND", req.getRequestURI()));
        }
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<BaseResponse<List<SessionDto>>> getByCourse(@PathVariable Long courseId, HttpServletRequest req) {
        return ResponseEntity.ok(BaseResponse.ok(sessionService.getByCourse(courseId), "OK", req.getRequestURI()));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<SessionDto>> create(@RequestBody SessionCreateRequest body, HttpServletRequest req) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(sessionService.create(body), "Session created", req.getRequestURI()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(BaseResponse.error(e.getMessage(), "BAD_REQUEST", req.getRequestURI()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<SessionDto>> update(@PathVariable Long id, @RequestBody SessionCreateRequest body, HttpServletRequest req) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(sessionService.update(id, body), "Session updated", req.getRequestURI()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(BaseResponse.error(e.getMessage(), "NOT_FOUND", req.getRequestURI()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id, HttpServletRequest req) {
        sessionService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok(null, "Session deleted", req.getRequestURI()));
    }
}

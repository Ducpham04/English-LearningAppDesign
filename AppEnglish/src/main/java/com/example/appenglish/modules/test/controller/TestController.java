package com.example.appenglish.modules.test.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.entity.User;
import com.example.appenglish.modules.test.dto.AttemptResultDto;
import com.example.appenglish.modules.test.dto.SubmitAttemptRequest;
import com.example.appenglish.modules.test.dto.TestDto;
import com.example.appenglish.modules.test.dto.TestTakeDto;
import com.example.appenglish.modules.test.service.TestService;
import com.example.appenglish.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API cho học sinh: xem danh sách test, lấy bài làm, nộp bài.
 */
@RestController
@RequestMapping("/v1/tests")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;
    private final UserRepository userRepository;

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        String email = auth.getName();
        return userRepository.findByEmail(email).map(User::getId).orElse(null);
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<TestDto>>> list(HttpServletRequest req) {
        String userId = getCurrentUserId();
        List<TestDto> list = userId != null ? testService.listForStudent(userId) : testService.listAll();
        return ResponseEntity.ok(BaseResponse.ok(list, "OK", req.getRequestURI()));
    }

    @GetMapping("/{id}/take")
    public ResponseEntity<BaseResponse<TestTakeDto>> getForTaking(@PathVariable Long id, HttpServletRequest req) {
        try {
            TestTakeDto dto = testService.getForTaking(id);
            return ResponseEntity.ok(BaseResponse.ok(dto, "OK", req.getRequestURI()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(BaseResponse.error(e.getMessage(), "NOT_FOUND", req.getRequestURI()));
        }
    }

    @PostMapping("/{id}/attempts")
    public ResponseEntity<BaseResponse<AttemptResultDto>> submitAttempt(
            @PathVariable Long id,
            @RequestBody SubmitAttemptRequest body,
            HttpServletRequest req) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body(BaseResponse.error("Unauthorized", "UNAUTHORIZED", req.getRequestURI()));
        }
        try {
            AttemptResultDto result = testService.submitAttempt(id, userId, body);
            return ResponseEntity.ok(BaseResponse.ok(result, "OK", req.getRequestURI()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(BaseResponse.error(e.getMessage(), "BAD_REQUEST", req.getRequestURI()));
        }
    }
}

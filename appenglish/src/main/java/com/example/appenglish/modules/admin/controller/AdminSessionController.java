package com.example.appenglish.modules.admin.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.modules.admin.dto.AdminSessionParticipantDto;
import com.example.appenglish.modules.admin.service.AdminSessionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/admin/sessions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminSessionController {

    AdminSessionService adminSessionService;

    @GetMapping("/{sessionId}/participants")
    public ResponseEntity<BaseResponse<List<AdminSessionParticipantDto>>> getParticipants(
            @PathVariable Long sessionId,
            HttpServletRequest req
    ) {
        List<AdminSessionParticipantDto> data = adminSessionService.getParticipants(sessionId);
        return ResponseEntity.ok(
                BaseResponse.ok(data, "OK", req.getRequestURI())
        );
    }

    @DeleteMapping("/{sessionId}/participants/{userId}")
    public ResponseEntity<BaseResponse<Void>> removeParticipant(
            @PathVariable Long sessionId,
            @PathVariable String userId,
            HttpServletRequest req
    ) {
        adminSessionService.removeParticipant(sessionId, userId);
        return ResponseEntity.ok(
                BaseResponse.ok(null, "REMOVED", req.getRequestURI())
        );
    }

    /**
     * Xóa toàn bộ bản ghi phát âm của các lesson trong session.
     * Admin cần gọi API này (và 2 API dưới nếu có dữ liệu) trước khi được phép xóa session.
     */
    @DeleteMapping("/{sessionId}/pronunciation-attempts")
    public ResponseEntity<BaseResponse<Object>> clearPronunciationAttempts(
            @PathVariable Long sessionId,
            HttpServletRequest req
    ) {
        try {
            long deleted = adminSessionService.clearPronunciationAttempts(sessionId);
            return ResponseEntity.ok(
                    BaseResponse.ok(java.util.Map.of("deletedCount", deleted), "Pronunciation attempts cleared", req.getRequestURI())
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(
                    BaseResponse.error(e.getMessage(), "NOT_FOUND", req.getRequestURI())
            );
        }
    }

    /** Xóa toàn bộ tiến độ bài học (user_lesson_progress) trong session. */
    @DeleteMapping("/{sessionId}/user-lesson-progress")
    public ResponseEntity<BaseResponse<Object>> clearUserLessonProgress(
            @PathVariable Long sessionId,
            HttpServletRequest req
    ) {
        try {
            long deleted = adminSessionService.clearUserLessonProgress(sessionId);
            return ResponseEntity.ok(
                    BaseResponse.ok(java.util.Map.of("deletedCount", deleted), "User lesson progress cleared", req.getRequestURI())
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(
                    BaseResponse.error(e.getMessage(), "NOT_FOUND", req.getRequestURI())
            );
        }
    }

    /** Xóa toàn bộ tiến độ hoàn thành session (user_session_progress). */
    @DeleteMapping("/{sessionId}/user-session-progress")
    public ResponseEntity<BaseResponse<Object>> clearUserSessionProgress(
            @PathVariable Long sessionId,
            HttpServletRequest req
    ) {
        try {
            long deleted = adminSessionService.clearUserSessionProgress(sessionId);
            return ResponseEntity.ok(
                    BaseResponse.ok(java.util.Map.of("deletedCount", deleted), "User session progress cleared", req.getRequestURI())
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(
                    BaseResponse.error(e.getMessage(), "NOT_FOUND", req.getRequestURI())
            );
        }
    }
}


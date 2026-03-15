package com.example.appenglish.modules.admin.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.modules.admin.dto.AdminCourseParticipantDto;
import com.example.appenglish.modules.admin.service.AdminCourseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/admin/courses")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminCourseController {

    AdminCourseService adminCourseService;

    @GetMapping("/{courseId}/participants")
    public ResponseEntity<BaseResponse<List<AdminCourseParticipantDto>>> getParticipants(
            @PathVariable Long courseId,
            HttpServletRequest req
    ) {
        List<AdminCourseParticipantDto> data = adminCourseService.getParticipants(courseId);
        return ResponseEntity.ok(
                BaseResponse.ok(data, "OK", req.getRequestURI())
        );
    }

    @DeleteMapping("/{courseId}/participants/{userId}")
    public ResponseEntity<BaseResponse<Void>> removeParticipant(
            @PathVariable Long courseId,
            @PathVariable String userId,
            HttpServletRequest req
    ) {
        adminCourseService.removeParticipant(courseId, userId);
        return ResponseEntity.ok(
                BaseResponse.ok(null, "REMOVED", req.getRequestURI())
        );
    }
}


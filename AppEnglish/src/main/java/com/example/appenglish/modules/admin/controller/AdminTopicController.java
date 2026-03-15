package com.example.appenglish.modules.admin.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.modules.admin.dto.AdminTopicParticipantDto;
import com.example.appenglish.modules.admin.service.AdminTopicService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/admin/topics")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminTopicController {

    AdminTopicService adminTopicService;

    @GetMapping("/{topicId}/participants")
    public ResponseEntity<BaseResponse<List<AdminTopicParticipantDto>>> getParticipants(
            @PathVariable Long topicId,
            HttpServletRequest req
    ) {
        List<AdminTopicParticipantDto> data = adminTopicService.getParticipants(topicId);
        return ResponseEntity.ok(
                BaseResponse.ok(data, "OK", req.getRequestURI())
        );
    }

    @DeleteMapping("/{topicId}/participants/{userId}")
    public ResponseEntity<BaseResponse<Void>> removeParticipant(
            @PathVariable Long topicId,
            @PathVariable String userId,
            HttpServletRequest req
    ) {
        adminTopicService.removeParticipant(topicId, userId);
        return ResponseEntity.ok(
                BaseResponse.ok(null, "REMOVED", req.getRequestURI())
        );
    }
}


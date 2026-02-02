package com.example.appenglish.modules.course.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.common.utils.SecurityUtils;
import com.example.appenglish.modules.course.dto.LessonCompleteRequest;
import com.example.appenglish.modules.course.dto.LessonCompleteResponse;
import com.example.appenglish.modules.course.dto.LessonDetailsResponse;
import com.example.appenglish.modules.course.service.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lessons")
@RequiredArgsConstructor
@Tag(name = "Lesson")
public class LessonController {

    private final LessonService service;

    @GetMapping("/{lessonId}")
    @Operation(summary = "Get lesson details")
    public ResponseEntity<BaseResponse<LessonDetailsResponse>> getLesson(
            @PathVariable Long lessonId,
            HttpServletRequest request) {
        
        LessonDetailsResponse data = service.getLessonDetails(lessonId);
        
        return ResponseEntity.ok(
            BaseResponse.ok(data, "Fetch lesson details successfully", request.getRequestURI())
        );
    }

    @PostMapping("/{lessonId}/complete")
    @Operation(summary = "Mark lesson as completed")
    public ResponseEntity<BaseResponse<LessonCompleteResponse>> completeLesson(
            @PathVariable Long lessonId,
            @RequestBody LessonCompleteRequest requestBody,
            HttpServletRequest request) {
        
        Long userId = SecurityUtils.getCurrentUserId(); 
        
        LessonCompleteResponse result = service.completeLesson(lessonId, userId, requestBody);
        
        String successMessage = "Lesson completed! +" + result.getXpEarned() + " XP";
        
        return ResponseEntity.ok(
            BaseResponse.ok(result, successMessage, request.getRequestURI())
        );
    }
}
package com.example.appenglish.modules.course.controller;

import com.example.appenglish.modules.course.dto.LessonDetailsResponse;
import com.example.appenglish.modules.course.dto.QuizSubmitRequest;
import com.example.appenglish.modules.course.service.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller xử lý các yêu cầu liên quan đến Lesson (Bài học).
 */
@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
@Tag(name = "Lesson")
public class LessonController {

    private final LessonService lessonService;

    /**
     * Lấy thông tin chi tiết của một bài học (Video, Quiz, Text).
     */
    @GetMapping("/{id}")
    public ResponseEntity<LessonDetailsResponse> getLessonDetails(
            @PathVariable 
            @Parameter(description = "ID của bài học", example = "1") Long id) {
        
        return ResponseEntity.ok(lessonService.getLessonDetails(id));
    }

    /**
     * POST /api/lessons/{id}/submit
     * Nộp kết quả bài Quiz.
     * @param id ID của bài học chứa bài test
     * @param userAnswers Map chứa QuestionID và OptionID người dùng chọn
     * @param userDetails Thông tin user lấy từ Token JWT
     */
    @ApiResponse(responseCode = "200")
@PostMapping("/{id}/submit")
public ResponseEntity<Map<String, Object>> submitQuiz(
        @PathVariable Long id,
        @RequestBody QuizSubmitRequest request, // Dùng DTO mới ở đây
        @AuthenticationPrincipal UserDetails userDetails) {
    
    // Giả sử lấy userId tạm thời là 1, bạn nên lấy từ userDetails thực tế
    Map<String, Object> result = lessonService.submitQuiz(id, 1L, request.getAnswers());
    
    return ResponseEntity.ok(result);
}
}
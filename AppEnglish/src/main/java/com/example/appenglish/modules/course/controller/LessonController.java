package com.example.appenglish.modules.course.controller;

import com.example.appenglish.modules.course.dto.LessonDetailsResponse;
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
@Tag(name = "Lesson", description = "Quản lý nội dung bài học và làm bài Quiz")
public class LessonController {

    private final LessonService lessonService;

    /**
     * Lấy thông tin chi tiết của một bài học (Video, Quiz, Text).
     */
    @Operation(summary = "Lấy chi tiết bài học", description = "Tự động ẩn đáp án đúng nếu là bài QUIZ để bảo mật.")
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
    @Operation(summary = "Nộp bài Quiz", description = "Gửi danh sách đáp án đã chọn để tính điểm và lưu lịch sử.")
    @ApiResponse(responseCode = "200", description = "Trả về số câu đúng và tổng điểm")
    @PostMapping("/{id}/submit")
    public ResponseEntity<Map<String, Object>> submitQuiz(
            @PathVariable Long id,
            @RequestBody Map<Long, Long> userAnswers,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // Giả sử email/username là duy nhất, lấy userId từ service dựa trên email
        // Ở đây chúng ta tạm gọi submitQuiz với logic đã viết trong LessonService
        // Lưu ý: Cần thêm logic lấy UserId thực tế từ UserDetails nếu cần
        
        // Tạm thời truyền Long.valueOf(1) hoặc một ID giả định nếu chưa có logic lấy ID từ UserDetails
        Map<String, Object> result = lessonService.submitQuiz(id, 1L, userAnswers);
        
        return ResponseEntity.ok(result);
    }
}
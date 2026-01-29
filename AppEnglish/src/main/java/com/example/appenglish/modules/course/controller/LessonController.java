package com.example.appenglish.modules.course.controller;

import com.example.appenglish.modules.course.service.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller xử lý các yêu cầu liên quan đến Lesson (Bài học).
 */
@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
@Tag(name = "Lesson", description = "Quản lý chi tiết bài học (Video, Quiz, Text)")
public class LessonController {

    private final LessonService lessonService;

    /**
     * GET /api/lessons/{id}
     * Lấy thông tin chi tiết của một bài học.
     * Tự động xử lý: 
     * - Nếu là VIDEO: Trả về kèm subtitlesUrl.
     * - Nếu là QUIZ: Trả về danh sách câu hỏi và ẩn đáp án đúng (isCorrect).
     * * @param id ID của bài học
     * @return Đối tượng chi tiết bài học dưới dạng Map động
     */
    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết bài học theo ID", 
               description = "Trả về dữ liệu linh hoạt tùy theo loại bài học (Video thì có sub, Quiz thì ẩn đáp án)")
    public ResponseEntity<Map<String, Object>> getLessonDetails(
            @PathVariable 
            @Parameter(description = "ID của bài học", example = "1") Long id) {
        
        // Gọi service xử lý logic lọc và ẩn dữ liệu nhạy cảm
        Map<String, Object> lessonDetails = lessonService.getLessonDetails(id);
        
        return ResponseEntity.ok(lessonDetails);
    }
}
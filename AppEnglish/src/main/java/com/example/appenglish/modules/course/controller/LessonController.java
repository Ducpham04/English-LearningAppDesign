package com.example.appenglish.modules.course.controller;

import com.example.appenglish.modules.course.dto.LessonCompleteRequest;
import com.example.appenglish.modules.course.dto.LessonCompleteResponse;
import com.example.appenglish.modules.course.dto.LessonDetailsResponse;
import com.example.appenglish.modules.course.service.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
@Tag(name = "Lesson", description = "Quản lý nội dung bài học và tiến độ hoàn thành")
public class LessonController {

    private final LessonService service;

    /**
     * Wrapper cho API 5.3 (Chi tiết bài học)
     */
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    @Schema(name = "LessonDataResponse", description = "Phản hồi chi tiết bài học")
    public static class LessonDataResponse {
        private boolean success;
        private LessonDetailsResponse data;
    }

    /**
     * Wrapper cho API 5.5 (Hoàn thành bài học & Gửi bài Quiz)
     */
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    @Schema(name = "LessonCompleteDataResponse", description = "Phản hồi sau khi hoàn thành bài học")
    public static class LessonCompleteDataResponse {
        private boolean success;
        private LessonCompleteResponse data;
        private String message;
    }

    @GetMapping("/{lessonId}")
    @Operation(
        summary = "5.3 Get Lesson Details",
        description = "Lấy toàn bộ nội dung chi tiết của một bài học bao gồm Video URL, Phụ đề chi tiết, Từ vựng mục tiêu và điểm số hiện tại của người dùng.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                         content = @Content(schema = @Schema(implementation = LessonDataResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy bài học")
        }
    )
    public ResponseEntity<LessonDataResponse> getLesson(@PathVariable Long lessonId) {
        var data = service.getLessonDetails(lessonId);
        return ResponseEntity.ok(LessonDataResponse.builder()
                .success(true)
                .data(data)
                .build());
    }

    @PostMapping("/{lessonId}/complete")
    @Operation(
        summary = "5.5 Complete Lesson & Submit Quiz",
        description = "Gửi yêu cầu hoàn thành bài học. Nếu bài học có Quiz (answers không rỗng), hệ thống sẽ tự động chấm điểm, lưu lịch sử, cộng XP và trả về thông tin bài học tiếp theo.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Hoàn thành bài học thành công", 
                         content = @Content(schema = @Schema(implementation = LessonCompleteDataResponse.class)))
        }
    )
    public ResponseEntity<LessonCompleteDataResponse> completeLesson(
            @PathVariable Long lessonId,
            @RequestBody LessonCompleteRequest request) {
        
        // Mock userId = 1L (Sẽ thay thế bằng SecurityContextHolder sau khi có Security)
        Long userId = 1L; 
        
        var result = service.completeLesson(lessonId, userId, request);
        
        return ResponseEntity.ok(LessonCompleteDataResponse.builder()
                .success(true)
                .data(result)
                .message("Lesson completed! +" + result.getXpEarned() + " XP")
                .build());
    }
}
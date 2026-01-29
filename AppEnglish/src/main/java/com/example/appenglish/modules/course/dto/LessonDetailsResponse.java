package com.example.appenglish.modules.course.dto;

import com.example.appenglish.enums.LessonType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thông tin chi tiết bài học và các nội dung liên quan (Quiz/Video)")
public class LessonDetailsResponse {

    @Schema(description = "ID của bài học (tự tăng từ 1)", example = "1")
    private Long lessonId;

    @Schema(description = "Tiêu đề bài học", example = "Present Simple Tense")
    private String title;

    @Schema(description = "Loại bài học", example = "QUIZ")
    private LessonType type;

    @Schema(description = "Thứ tự bài học trong chương", example = "1")
    private Integer orderIndex;
    
    // --- QUIZ INFO ---
    
    @Schema(description = "ID của bài Quiz đi kèm bài học này", example = "101")
    private Long quizId;

    @Schema(description = "Danh sách câu hỏi (chỉ có dữ liệu nếu type là QUIZ)")
    private List<QuestionResponse> questions;

    // --- PHẦN NỘI DUNG LINH HOẠT ---

    @Schema(
        description = "Dữ liệu cấu hình linh hoạt (Metadata)",
        example = "{ \"duration\": \"15m\", \"difficulty\": \"Easy\", \"vocab_count\": 12 }"
    )
    private Map<String, Object> metadata; 

    @Schema(description = "Đường dẫn Video (chỉ có nếu type là VIDEO)", example = "https://youtube.com/watch?v=abc")
    private String videoUrl;

    // --- INNER DTOs ---

    @Data
    @Builder
    @Schema(description = "Thông tin câu hỏi")
    public static class QuestionResponse {
        @Schema(description = "ID câu hỏi (tự tăng)", example = "1")
        private Long id;

        @Schema(description = "Nội dung câu hỏi", example = "What is the capital of France?")
        private String content;

        @Schema(description = "Danh sách các lựa chọn trả lời")
        private List<OptionResponse> options;
    }

    @Data
    @Builder
    @Schema(description = "Thông tin lựa chọn trả lời")
    public static class OptionResponse {
        @Schema(description = "ID lựa chọn (tự tăng)", example = "1")
        private Long id;

        @Schema(description = "Nội dung lựa chọn", example = "Paris")
        private String content;
    }
}
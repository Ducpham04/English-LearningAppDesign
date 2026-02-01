package com.example.appenglish.modules.course.dto;

import com.example.appenglish.enums.LessonType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonDetailsResponse {

    @Schema(example = "1")
    private Long lessonId;

    @Schema(example = "1")
    private Long sessionId;

    @Schema(example = "101", description = "ID của bài tập đi kèm (nếu có)")
    private Long quizId;

    @Schema(example = "Greetings & Introductions")
    private String title;

    @Schema(example = "video")
    private LessonType type;

    @Schema(example = "Learn basic greetings and how to introduce yourself")
    private String description;

    @Schema(example = "5", description = "Thời lượng ước tính (phút)")
    private Integer duration;

    @Schema(example = "completed")
    private String status;

    @Schema(example = "1")
    private Integer order;

    @Schema(example = "https://cdn.english-ai.com/videos/lesson1.mp4")
    private String videoUrl;
    
    // --- Phần dành cho Video Lesson ---
    private List<SubtitleResponse> subtitles;
    
    @Schema(example = "[\"hello\", \"my\", \"name\", \"is\"]")
    private List<String> targetWords;

    // --- Phần dành cho Quiz Lesson (Đáp án đúng đã bị ẩn) ---
    @Schema(description = "Danh sách câu hỏi bài tập")
    private List<QuestionResponse> questions;

    // --- Phần tiến độ người dùng ---
    @Schema(example = "70", description = "Điểm tối thiểu để hoàn thành")
    private Integer minScore;

    @Schema(example = "85.5", description = "Điểm số cao nhất của người dùng")
    private Double userScore;

    private LocalDateTime completedAt;
    
    private Map<String, Object> metadata;

    // --- Inner Classes dành cho Subtitles ---

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubtitleResponse {
        private String id;
        private String text;
        private Double startTime;
        private Double endTime;
        private List<WordDetail> words;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WordDetail {
        private String text;
        private Double startTime;
        private Double endTime;
        private String pronunciation;
        private String meaning;
    }

    // --- Inner Classes dành cho Quiz (Bảo mật cao) ---

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionResponse {
        private Long id;
        private String content;
        private List<OptionResponse> options;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionResponse {
        private Long id;
        @Schema(example = "Hello")
        private String content;
    }
}
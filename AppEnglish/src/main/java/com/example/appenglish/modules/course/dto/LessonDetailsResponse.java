package com.example.appenglish.modules.course.dto;

import com.example.appenglish.enums.LessonType;
import lombok.*;
import java.util.List;

@Data
@Builder
public class LessonDetailsResponse {
    private Long id;
    private String title;
    private LessonType type;
    private String content; // Dùng cho bài học dạng văn bản
    
    // Thông tin cho Video
    private String videoUrl;
    private String subtitlesUrl;
    
    // Thông tin cho Quiz
    private List<QuestionResponse> questions;

    @Data
    @Builder
    public static class QuestionResponse {
        private Long id;
        private String content;
        private List<OptionResponse> options;
    }

    @Data
    @Builder
    public static class OptionResponse {
        private Long id;
        private String content;
        // KHÔNG bao gồm trường isCorrect ở đây để ẩn đáp án
    }
}
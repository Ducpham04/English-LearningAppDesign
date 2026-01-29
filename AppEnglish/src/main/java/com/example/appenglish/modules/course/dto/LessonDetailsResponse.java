package com.example.appenglish.modules.course.dto;

import com.example.appenglish.enums.LessonType;
import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonDetailsResponse {
    private Long id;
    private String title;
    private LessonType type;
    
    // Thêm trường này để fix lỗi "undefined"
    private Long quizId; 
    
    private String videoUrl;
    private String subtitlesUrl;
    private String htmlContent;
    
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
    }
}
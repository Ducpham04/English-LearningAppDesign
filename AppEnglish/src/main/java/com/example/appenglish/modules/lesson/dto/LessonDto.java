package com.example.appenglish.modules.lesson.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class LessonDto {
    private Long id;
    private String title;
    private String description;
    private String type;        // VIDEO, QUIZ, VOCAB, SPEAKING
    private Integer orderIndex;
    private Integer durationMinutes;
    private Long sessionId;
    private Long courseId;

    // Video fields - chỉ có khi type = VIDEO
    private String videoUrl;        // S3 URI audio
    private String youtubeUrl;      // YouTube URL gốc để embed
    private List<Map<String, Object>> transcriptJson;  // [{sentenceIndex, start(ms), end(ms), text, viText, ipa}]
    private String s3Status;        // PROCESSING / COMPLETED / FAILED (nếu có)
}

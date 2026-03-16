package com.example.appenglish.modules.lesson.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class LessonVideoRequest {
    private String videoUrl;
    // Mỗi entry: {"start": 0.0, "end": 3.5, "text": "Hello", "speaker": "A", "translation": "Xin chào"}
    private List<Map<String, Object>> transcriptJson;
}

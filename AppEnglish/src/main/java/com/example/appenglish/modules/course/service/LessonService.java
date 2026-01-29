package com.example.appenglish.modules.course.service;

import com.example.appenglish.entity.*;
import com.example.appenglish.enums.LessonType;
import com.example.appenglish.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;

    public Map<String, Object> getLessonDetails(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học"));

        Map<String, Object> details = new HashMap<>();
        details.put("id", lesson.getId());
        details.put("title", lesson.getTitle());
        details.put("type", lesson.getType());

        // 1. Logic xử lý cho bài học dạng VIDEO (Fetch kèm subtitles)
        if (lesson.getType() == LessonType.VIDEO && lesson.getVideo() != null) {
            LessonVideo video = lesson.getVideo();
            details.put("videoUrl", video.getVideoUrl());
            details.put("subtitlesUrl", video.getSubtitlesUrl());
        }

        // 2. Logic xử lý cho bài học dạng QUIZ (Ẩn đáp án đúng)
        else if (lesson.getType() == LessonType.QUIZ && lesson.getTest() != null) {
            Test test = lesson.getTest();
            
            List<Map<String, Object>> questions = test.getQuestions().stream().map(q -> {
                Map<String, Object> qMap = new HashMap<>();
                qMap.put("id", q.getId());
                qMap.put("content", q.getContent());
                
                // Map options và ẩn trường isCorrect
                List<Map<String, Object>> options = q.getOptions().stream().map(o -> {
                    Map<String, Object> oMap = new HashMap<>();
                    oMap.put("id", o.getId());
                    oMap.put("content", o.getContent());
                    // Tuyệt đối không put trường "isCorrect" vào đây để bảo mật
                    return oMap;
                }).collect(Collectors.toList());
                
                qMap.put("options", options);
                return qMap;
            }).collect(Collectors.toList());
            
            details.put("questions", questions);
        }
        
        // 3. Logic xử lý cho bài học dạng TEXT (Nếu có nội dung văn bản)
        else {
            // Giả sử bài học thường có nội dung mô tả
            // details.put("content", lesson.getDescription()); 
        }

        return details;
    }
}
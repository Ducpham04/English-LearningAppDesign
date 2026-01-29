package com.example.appenglish.modules.course.service;

import com.example.appenglish.entity.Lesson;
import com.example.appenglish.entity.LessonVideo;
import com.example.appenglish.enums.LessonType;
import com.example.appenglish.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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

        // Logic fetch kèm subtitles nếu là video
        if (lesson.getType() == LessonType.VIDEO && lesson.getVideo() != null) {
            LessonVideo video = lesson.getVideo();
            details.put("videoUrl", video.getVideoUrl());
            details.put("subtitlesUrl", video.getSubtitlesUrl());
        }

        return details;
    }
}
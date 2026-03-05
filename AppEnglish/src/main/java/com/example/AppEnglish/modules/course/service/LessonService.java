package com.example.AppEnglish.modules.course.service;

import com.example.AppEnglish.modules.course.dto.LessonDetailDTO;
import com.example.AppEnglish.modules.course.dto.LessonQuizDetailDTO;
import com.example.AppEnglish.modules.course.dto.LessonVideoDetailDTO;

import java.util.List;

public interface LessonService {

    LessonDetailDTO getLessonDetail(Long lessonId);

    List<LessonVideoDetailDTO> getVideoLessons();

    List<LessonQuizDetailDTO> getQuizLessons();
}

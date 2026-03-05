package com.example.AppEnglish.modules.course.service.impl;

import com.example.AppEnglish.common.exception.ApiException;
import com.example.AppEnglish.entity.*;
import com.example.AppEnglish.enums.LessonType;
import com.example.AppEnglish.modules.course.dto.LessonDetailDTO;
import com.example.AppEnglish.modules.course.dto.LessonQuizDetailDTO;
import com.example.AppEnglish.modules.course.dto.LessonVideoDetailDTO;
import com.example.AppEnglish.modules.course.service.LessonService;
import com.example.AppEnglish.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final TestRepository testRepository;
    private final TestQuestionRepository testQuestionRepository;
    private final TestOptionRepository testOptionRepository;

    @Override
    public LessonDetailDTO getLessonDetail(Long lessonId) {
        Lesson lesson = lessonRepository.findByIdWithVideo(lessonId)
                .orElseThrow(() -> new ApiException("Lesson not found: " + lessonId, "LESSON_NOT_FOUND"));

        LessonDetailDTO.LessonDetailDTOBuilder builder = LessonDetailDTO.baseFromEntity(lesson);

        if (lesson.getType() == LessonType.VIDEO) {
            buildVideoDetail(lesson, builder);
        }

        if (lesson.getType() == LessonType.QUIZ) {
            buildQuizDetail(lesson, builder);
        }

        return builder.build();
    }

    @Override
    public List<LessonVideoDetailDTO> getVideoLessons() {
        List<Lesson> videoLessons = lessonRepository.findAllWithVideoByType(LessonType.VIDEO);

        return videoLessons.stream()
                .map(LessonVideoDetailDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<LessonQuizDetailDTO> getQuizLessons() {
        List<Lesson> quizLessons = lessonRepository.findByTypeOrderByOrderIndexAsc(LessonType.QUIZ);

        return quizLessons.stream()
                .map(lesson -> {
                    Test test = testRepository.findByLesson_Id(lesson.getId()).orElse(null);
                    if (test == null) {
                        log.warn("Lesson {} is QUIZ type but has no associated Test", lesson.getId());
                        return LessonQuizDetailDTO.builder()
                                .lessonId(lesson.getId())
                                .lessonTitle(lesson.getTitle())
                                .build();
                    }

                    List<TestQuestion> questions = testQuestionRepository.findByTest_IdOrderByIdAsc(test.getId());

                    List<LessonQuizDetailDTO.QuestionDTO> questionDTOs = questions.stream()
                            .map(q -> {
                                List<LessonQuizDetailDTO.OptionDTO> options = null;
                                if ("MCQ".equals(q.getType().name())) {
                                    options = testOptionRepository.findByQuestion_Id(q.getId()).stream()
                                            .map(LessonQuizDetailDTO.OptionDTO::fromEntity)
                                            .collect(Collectors.toList());
                                }
                                return LessonQuizDetailDTO.QuestionDTO.fromEntity(q, options);
                            })
                            .collect(Collectors.toList());

                    return LessonQuizDetailDTO.fromEntity(lesson.getId(), lesson.getTitle(), test, questionDTOs);
                })
                .collect(Collectors.toList());
    }

    private void buildVideoDetail(Lesson lesson, LessonDetailDTO.LessonDetailDTOBuilder builder) {
        LessonVideo video = lesson.getVideo();
        if (video == null) {
            log.warn("Lesson {} is VIDEO type but has no associated LessonVideo", lesson.getId());
            return;
        }

        builder.videoUrl(video.getVideoUrl());
        builder.subtitles(LessonDetailDTO.parseSubtitles(video.getTranscriptJson()));
        builder.targetWords(LessonDetailDTO.parseTargetWords(lesson.getContentPayload()));
    }

    private void buildQuizDetail(Lesson lesson, LessonDetailDTO.LessonDetailDTOBuilder builder) {
        Test test = testRepository.findByLesson_Id(lesson.getId()).orElse(null);
        if (test == null) {
            log.warn("Lesson {} is QUIZ type but has no associated Test", lesson.getId());
            return;
        }

        List<TestQuestion> questions = testQuestionRepository.findByTest_IdOrderByIdAsc(test.getId());

        List<LessonDetailDTO.QuestionDTO> questionDTOs = questions.stream()
                .map(q -> {
                    List<LessonDetailDTO.OptionDTO> options = null;
                    if ("MCQ".equals(q.getType().name())) {
                        options = testOptionRepository.findByQuestion_Id(q.getId()).stream()
                                .map(LessonDetailDTO.OptionDTO::fromEntity)
                                .collect(Collectors.toList());
                    }
                    return LessonDetailDTO.QuestionDTO.fromEntity(q, options);
                })
                .collect(Collectors.toList());

        builder.quiz(LessonDetailDTO.QuizDetailDTO.fromEntity(test, questionDTOs));
    }
}

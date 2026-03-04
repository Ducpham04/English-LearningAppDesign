package com.example.appenglish.modules.lesson.service;

import com.example.appenglish.entity.Curriculum;
import com.example.appenglish.entity.Lesson;
import com.example.appenglish.entity.Session;
import com.example.appenglish.modules.lesson.dto.LessonDto;
import com.example.appenglish.modules.lesson.dto.LessonRequest;
import com.example.appenglish.repository.CurriculumRepository;
import com.example.appenglish.repository.LessonRepository;
import com.example.appenglish.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final CurriculumRepository curriculumRepository;
    private final SessionRepository sessionRepository;

    @Transactional(readOnly = true)
    public List<LessonDto> getAllLessons() {
        return lessonRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LessonDto getLessonById(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + id));
        return mapToDto(lesson);
    }

    @Transactional(readOnly = true)
    public List<LessonDto> getLessonsByCourse(Long courseId) {
        return lessonRepository.findByCourseId(courseId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LessonDto> getLessonsBySession(Long sessionId) {
        return lessonRepository.findBySessionId(sessionId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public LessonDto createLesson(LessonRequest request) {
        Curriculum course = null;
        Session session = null;

        if (request.getCourseId() != null) {
            course = curriculumRepository.findById(request.getCourseId())
                    .orElseThrow(() -> new RuntimeException("Course not found with id: " + request.getCourseId()));
        }

        if (request.getSessionId() != null) {
            session = sessionRepository.findById(request.getSessionId())
                    .orElseThrow(() -> new RuntimeException("Session not found with id: " + request.getSessionId()));
        }

        Lesson lesson = Lesson.builder()
                .title(request.getTitle())
                .course(course)
                .session(session)
                .type(request.getType())
                .orderIndex(request.getOrderIndex())
                .contentPayload(request.getContentPayload())
                .build();

        Lesson saved = lessonRepository.save(lesson);
        return mapToDto(saved);
    }

    @Transactional
    public LessonDto updateLesson(Long id, LessonRequest request) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + id));

        if (request.getTitle() != null) {
            lesson.setTitle(request.getTitle());
        }
        if (request.getType() != null) {
            lesson.setType(request.getType());
        }
        if (request.getOrderIndex() != null) {
            lesson.setOrderIndex(request.getOrderIndex());
        }
        if (request.getContentPayload() != null) {
            lesson.setContentPayload(request.getContentPayload());
        }
        if (request.getCourseId() != null) {
            Curriculum course = curriculumRepository.findById(request.getCourseId())
                    .orElseThrow(() -> new RuntimeException("Course not found with id: " + request.getCourseId()));
            lesson.setCourse(course);
        }
        if (request.getSessionId() != null) {
            Session session = sessionRepository.findById(request.getSessionId())
                    .orElseThrow(() -> new RuntimeException("Session not found with id: " + request.getSessionId()));
            lesson.setSession(session);
        }

        Lesson saved = lessonRepository.save(lesson);
        return mapToDto(saved);
    }

    @Transactional
    public void deleteLesson(Long id) {
        if (!lessonRepository.existsById(id)) {
            throw new RuntimeException("Lesson not found with id: " + id);
        }
        lessonRepository.deleteById(id);
    }

    private LessonDto mapToDto(Lesson lesson) {
        return LessonDto.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .courseId(lesson.getCourse() != null ? lesson.getCourse().getId() : null)
                .courseName(lesson.getCourse() != null ? lesson.getCourse().getTitle() : null)
                .sessionId(lesson.getSession() != null ? lesson.getSession().getId() : null)
                .sessionName(lesson.getSession() != null ? lesson.getSession().getTitle() : null)
                .type(lesson.getType())
                .orderIndex(lesson.getOrderIndex())
                .contentPayload(lesson.getContentPayload())
                .build();
    }
}

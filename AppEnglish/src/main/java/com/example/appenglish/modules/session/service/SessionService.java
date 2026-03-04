package com.example.appenglish.modules.session.service;

import com.example.appenglish.entity.Curriculum;
import com.example.appenglish.entity.Session;
import com.example.appenglish.modules.session.dto.SessionDto;
import com.example.appenglish.modules.session.dto.SessionRequest;
import com.example.appenglish.repository.CurriculumRepository;
import com.example.appenglish.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final CurriculumRepository curriculumRepository;

    @Transactional(readOnly = true)
    public List<SessionDto> getAllSessions() {
        return sessionRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SessionDto getSessionById(Long id) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + id));
        return mapToDto(session);
    }

    @Transactional(readOnly = true)
    public List<SessionDto> getSessionsByCourse(Long courseId) {
        return sessionRepository.findByCourseId(courseId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public SessionDto createSession(SessionRequest request) {
        Curriculum course = curriculumRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + request.getCourseId()));

        Session session = Session.builder()
                .title(request.getTitle())
                .orderIndex(request.getOrderIndex())
                .course(course)
                .build();

        Session saved = sessionRepository.save(session);
        return mapToDto(saved);
    }

    @Transactional
    public SessionDto updateSession(Long id, SessionRequest request) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + id));

        if (request.getTitle() != null) {
            session.setTitle(request.getTitle());
        }
        if (request.getOrderIndex() != null) {
            session.setOrderIndex(request.getOrderIndex());
        }
        if (request.getCourseId() != null) {
            Curriculum course = curriculumRepository.findById(request.getCourseId())
                    .orElseThrow(() -> new RuntimeException("Course not found with id: " + request.getCourseId()));
            session.setCourse(course);
        }

        Session saved = sessionRepository.save(session);
        return mapToDto(saved);
    }

    @Transactional
    public void deleteSession(Long id) {
        if (!sessionRepository.existsById(id)) {
            throw new RuntimeException("Session not found with id: " + id);
        }
        sessionRepository.deleteById(id);
    }

    private SessionDto mapToDto(Session session) {
        int lessonCount = session.getLessons() != null ? session.getLessons().size() : 0;
        return SessionDto.builder()
                .id(session.getId())
                .title(session.getTitle())
                .orderIndex(session.getOrderIndex())
                .courseId(session.getCourse() != null ? session.getCourse().getId() : null)
                .courseName(session.getCourse() != null ? session.getCourse().getTitle() : null)
                .lessonCount(lessonCount)
                .build();
    }
}

package com.example.appenglish.modules.session.service;

import com.example.appenglish.entity.Curriculum;
import com.example.appenglish.entity.Session;
import com.example.appenglish.modules.session.dto.SessionCreateRequest;
import com.example.appenglish.modules.session.dto.SessionDto;
import com.example.appenglish.repository.CurriculumRepository;
import com.example.appenglish.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final CurriculumRepository curriculumRepository;

    public List<SessionDto> getAll() {
        return sessionRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public SessionDto getById(Long id) {
        return sessionRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Session not found: " + id));
    }

    public List<SessionDto> getByCourse(Long courseId) {
        return sessionRepository.findByCourseIdOrderByOrderIndexAsc(courseId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public SessionDto create(SessionCreateRequest req) {
        Curriculum course = curriculumRepository.findById(req.getCourseId())
                .orElseThrow(() -> new RuntimeException("Curriculum not found: " + req.getCourseId()));
        Session session = new Session();
        session.setTitle(req.getTitle());
        session.setCourse(course);
        session.setOrderIndex(req.getOrderIndex() != null ? req.getOrderIndex() : 1);
        return toDto(sessionRepository.save(session));
    }

    public SessionDto update(Long id, SessionCreateRequest req) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found: " + id));
        if (req.getTitle() != null) session.setTitle(req.getTitle());
        if (req.getOrderIndex() != null) session.setOrderIndex(req.getOrderIndex());
        if (req.getCourseId() != null) {
            Curriculum course = curriculumRepository.findById(req.getCourseId())
                    .orElseThrow(() -> new RuntimeException("Curriculum not found: " + req.getCourseId()));
            session.setCourse(course);
        }
        return toDto(sessionRepository.save(session));
    }

    public void delete(Long id) {
        sessionRepository.deleteById(id);
    }

    private SessionDto toDto(Session s) {
        return SessionDto.builder()
                .id(s.getId())
                .title(s.getTitle())
                .orderIndex(s.getOrderIndex())
                .courseId(s.getCourse() != null ? s.getCourse().getId() : null)
                .courseTitle(s.getCourse() != null ? s.getCourse().getTitle() : null)
                .lessonCount(s.getLessons() != null ? s.getLessons().size() : 0)
                .build();
    }
}

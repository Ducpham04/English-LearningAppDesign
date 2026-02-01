package com.example.appenglish.modules.course.service;

import com.example.appenglish.entity.Lesson;
import com.example.appenglish.entity.Session;
import com.example.appenglish.modules.session.dto.SessionResponse;
import com.example.appenglish.modules.vocabulary.dto.VocabularyResponse;
import com.example.appenglish.repository.SessionRepository;
import com.example.appenglish.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final LessonRepository lessonRepository;

    public SessionResponse getSessionDetails(Long sessionId, Long userId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + sessionId));

        List<Lesson> lessonEntities = lessonRepository.findBySessionIdOrderByOrderIndexAsc(sessionId);
        
        // FIX: Khai báo rõ kiểu dữ liệu cho List và trong .map()
        List<SessionResponse.SessionLessonResponse> lessonResponses = lessonEntities.stream()
                .map(l -> SessionResponse.SessionLessonResponse.builder() // Gọi tường minh
                        .id(l.getId())
                        .title(l.getTitle())
                        .type(l.getType() != null ? l.getType().name().toLowerCase() : "video")
                        .description(l.getDescription())
                        .duration(l.getDurationFromPayload()) 
                        .order(l.getOrderIndex())
                        .status("completed")
                        .score(85.0)
                        .build())
                .collect(Collectors.toList());

        return SessionResponse.builder()
                .id(session.getId())
                .title(session.getTitle())
                .description(session.getDescription())
                .level("beginner")
                .thumbnail(session.getThumbnail())
                .totalLessons(lessonEntities.size())
                .completedLessons(3L)
                .lessons(lessonResponses)
                .vocabularyCount(25)
                .userProgress(SessionResponse.UserProgressData.builder() // Gọi tường minh
                        .completion(60.0)
                        .averageScore(88.5)
                        .timeSpent(15)
                        .build())
                .build();
    }

    public SessionResponse.SessionListWrapper getAllSessions(String level, String status, Pageable pageable) {
        Page<Session> sessionPage = sessionRepository.findAll(pageable);
        
        List<SessionResponse> sessions = sessionPage.getContent().stream()
                .map(this::toResponseBasic)
                .collect(Collectors.toList());

        return SessionResponse.SessionListWrapper.builder() // Gọi tường minh
                .sessions(sessions)
                .pagination(VocabularyResponse.PaginationData.builder()
                        .page(sessionPage.getNumber() + 1)
                        .limit(sessionPage.getSize())
                        .total(sessionPage.getTotalElements())
                        .totalPages(sessionPage.getTotalPages())
                        .build())
                .build();
    }

    private SessionResponse toResponseBasic(Session s) {
        return SessionResponse.builder()
                .id(s.getId())
                .title(s.getTitle())
                .description(s.getDescription())
                .thumbnail(s.getThumbnail())
                .level("beginner")
                .status("locked")
                .order(s.getOrderIndex())
                .totalLessons(5)
                .completedLessons(0L)
                .build();
    }
}
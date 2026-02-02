package com.example.appenglish.modules.course.service;

import com.example.appenglish.entity.Lesson;
import com.example.appenglish.entity.Session;
import com.example.appenglish.modules.session.dto.SessionResponse;
import com.example.appenglish.modules.vocabulary.dto.VocabularyResponse;
import com.example.appenglish.repository.SessionRepository;
import com.example.appenglish.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final LessonRepository lessonRepository;

    /**
     * Lấy chi tiết một Session kèm danh sách bài học và tiến độ user
     */
    public SessionResponse getSessionDetails(Long sessionId, Long userId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + sessionId));

        List<Lesson> lessonEntities = lessonRepository.findBySessionIdOrderByOrderIndexAsc(sessionId);
        
        List<SessionResponse.SessionLessonResponse> lessonResponses = lessonEntities.stream()
                .map(l -> SessionResponse.SessionLessonResponse.builder()
                        .id(l.getId())
                        .title(l.getTitle())
                        .type(l.getType() != null ? l.getType().name().toLowerCase() : "video")
                        .description(l.getDescription())
                        .duration(l.getDurationFromPayload()) 
                        .order(l.getOrderIndex())
                        .status("completed") // Mock status
                        .score(85.0)         // Mock score
                        .build())
                .collect(Collectors.toList());

        return SessionResponse.builder()
                .id(session.getId())
                .title(session.getTitle())
                .description(session.getDescription())
                .level("beginner")
                .thumbnail(session.getThumbnail())
                .totalLessons(lessonEntities.size())
                .completedLessons(3L) // Mock
                .lessons(lessonResponses)
                .vocabularyCount(25)   // Mock
                .userProgress(SessionResponse.UserProgressData.builder()
                        .completion(60.0)
                        .averageScore(88.5)
                        .timeSpent(15)
                        .build())
                .build();
    }

    /**
     * Lấy danh sách tất cả các Session có phân trang
     */
    public SessionResponse.SessionListWrapper getAllSessions(int page, int limit, Long userId) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Session> sessionPage = sessionRepository.findAll(pageable);
        
        return buildListWrapper(sessionPage);
    }

    /**
     * Lấy danh sách Session theo Course ID có phân trang
     */
    public SessionResponse.SessionListWrapper getSessionsByCourse(Long courseId, int page, int limit, Long userId) {
        Pageable pageable = PageRequest.of(page - 1, limit);

        Page<Session> sessionPage = sessionRepository.findAll(pageable);
        
        return buildListWrapper(sessionPage);
    }

    /**
     * Helper: Chuyển đổi từ Page sang Wrapper DTO
     */
    private SessionResponse.SessionListWrapper buildListWrapper(Page<Session> sessionPage) {
        List<SessionResponse> sessions = sessionPage.getContent().stream()
                .map(this::toResponseBasic)
                .collect(Collectors.toList());

        return SessionResponse.SessionListWrapper.builder()
                .sessions(sessions)
                .pagination(VocabularyResponse.PaginationData.builder()
                        .page(sessionPage.getNumber() + 1)
                        .limit(sessionPage.getSize())
                        .total(sessionPage.getTotalElements())
                        .totalPages(sessionPage.getTotalPages())
                        .build())
                .build();
    }

    /**
     * Helper: Map thông tin cơ bản của Session cho danh sách
     */
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
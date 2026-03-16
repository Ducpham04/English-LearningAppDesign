package com.example.appenglish.modules.admin.service;

import com.example.appenglish.entity.Session;
import com.example.appenglish.entity.UserLessonProgress;
import com.example.appenglish.modules.admin.dto.AdminSessionParticipantDto;
import com.example.appenglish.repository.LessonRepository;
import com.example.appenglish.repository.PronunciationAttemptRepository;
import com.example.appenglish.repository.SessionRepository;
import com.example.appenglish.repository.UserLessonProgressRepository;
import com.example.appenglish.repository.UserSessionProgressRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminSessionService {

    SessionRepository sessionRepository;
    LessonRepository lessonRepository;
    UserLessonProgressRepository userLessonProgressRepository;
    UserSessionProgressRepository userSessionProgressRepository;
    PronunciationAttemptRepository pronunciationAttemptRepository;

    @Transactional(readOnly = true)
    public List<AdminSessionParticipantDto> getParticipants(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

        List<UserLessonProgress> progress = userLessonProgressRepository.findByLesson_Session_Id(sessionId);

        Map<String, List<UserLessonProgress>> byUser = progress.stream()
                .filter(p -> p.getUser() != null)
                .collect(Collectors.groupingBy(p -> p.getUser().getId()));

        int totalLessons = lessonRepository.findBySessionIdOrderByOrderIndexAsc(sessionId).size();

        return byUser.entrySet().stream()
                .map(e -> {
                    String userId = e.getKey();
                    List<UserLessonProgress> list = e.getValue();
                    if (list.isEmpty()) return null;
                    var u = list.get(0).getUser();

                    int completed = list.size();
                    boolean sessionCompleted = userSessionProgressRepository
                            .existsByUser_IdAndSession_Id(userId, sessionId);
                    LocalDateTime lastActivity = list.stream()
                            .map(UserLessonProgress::getCompletedAt)
                            .filter(Objects::nonNull)
                            .max(LocalDateTime::compareTo)
                            .orElse(null);

                    return AdminSessionParticipantDto.builder()
                            .userId(null) // username/id chuỗi – FE dùng email/name để nhận diện
                            .fullName(u.getFullName())
                            .email(u.getEmail())
                            .avatarUrl(u.getAvatarUrl())
                            .sessionId(session.getId())
                            .sessionTitle(session.getTitle())
                            .completedLessons(completed)
                            .totalLessons(totalLessons)
                            .sessionCompleted(sessionCompleted)
                            .lastActivityAt(lastActivity != null ? lastActivity.atZone(java.time.ZoneId.systemDefault()).toInstant() : null)
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeParticipant(Long sessionId, String userId) {
        // Xóa toàn bộ progress bài học trong session đó
        userLessonProgressRepository.deleteByUser_IdAndLesson_Session_Id(userId, sessionId);
        // Xóa sessionProgress nếu có
        userSessionProgressRepository.deleteByUser_IdAndSession_Id(userId, sessionId);
    }

    /** Xóa toàn bộ bản ghi phát âm của các lesson thuộc session. Admin gọi trước khi được phép xóa session. */
    @Transactional
    public long clearPronunciationAttempts(Long sessionId) {
        sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));
        long count = pronunciationAttemptRepository.countByLesson_Session_Id(sessionId);
        pronunciationAttemptRepository.deleteByLesson_Session_Id(sessionId);
        return count;
    }

    /** Xóa toàn bộ tiến độ bài học (user_lesson_progress) của session. Admin gọi trước khi được phép xóa session. */
    @Transactional
    public long clearUserLessonProgress(Long sessionId) {
        sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));
        long count = userLessonProgressRepository.countByLesson_Session_Id(sessionId);
        userLessonProgressRepository.deleteByLesson_Session_Id(sessionId);
        return count;
    }

    /** Xóa toàn bộ tiến độ hoàn thành session (user_session_progress). Admin gọi trước khi được phép xóa session. */
    @Transactional
    public long clearUserSessionProgress(Long sessionId) {
        sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));
        long count = userSessionProgressRepository.countBySession_Id(sessionId);
        userSessionProgressRepository.deleteBySession_Id(sessionId);
        return count;
    }
}


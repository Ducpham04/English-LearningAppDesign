package com.example.appenglish.modules.session.service;

import com.example.appenglish.entity.Curriculum;
import com.example.appenglish.entity.Lesson;
import com.example.appenglish.entity.Session;
import com.example.appenglish.entity.Test;
import com.example.appenglish.common.exception.ApiException;
import com.example.appenglish.modules.session.dto.SessionCreateRequest;
import com.example.appenglish.modules.session.dto.SessionDetailDto;
import com.example.appenglish.modules.session.dto.SessionDto;
import com.example.appenglish.repository.CurriculumRepository;
import com.example.appenglish.repository.LessonRepository;
import com.example.appenglish.repository.PronunciationAttemptRepository;
import com.example.appenglish.repository.SessionRepository;
import com.example.appenglish.repository.TestRepository;
import com.example.appenglish.repository.UserLessonProgressRepository;
import com.example.appenglish.repository.UserSessionProgressRepository;
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
    private final LessonRepository lessonRepository;
    private final PronunciationAttemptRepository pronunciationAttemptRepository;
    private final UserLessonProgressRepository userLessonProgressRepository;
    private final UserSessionProgressRepository userSessionProgressRepository;
    private final TestRepository testRepository;

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

    /** Chi tiết session kèm thông tin liên quan: bài học, số bản ghi phát âm, tiến độ. */
    @Transactional(readOnly = true)
    public SessionDetailDto getDetail(Long id) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found: " + id));
        List<Lesson> lessons = lessonRepository.findBySessionIdOrderByOrderIndexAsc(id);

        List<SessionDetailDto.LessonSummaryDto> lessonSummaries = lessons.stream()
                .map(l -> SessionDetailDto.LessonSummaryDto.builder()
                        .id(l.getId())
                        .title(l.getTitle())
                        .orderIndex(l.getOrderIndex())
                        .type(l.getType() != null ? l.getType().name() : null)
                        .pronunciationAttemptCount(pronunciationAttemptRepository.countByLesson_Id(l.getId()))
                        .build())
                .collect(Collectors.toList());

        return SessionDetailDto.builder()
                .id(session.getId())
                .title(session.getTitle())
                .orderIndex(session.getOrderIndex())
                .courseId(session.getCourse() != null ? session.getCourse().getId() : null)
                .courseTitle(session.getCourse() != null ? session.getCourse().getTitle() : null)
                .topicId(session.getTopic() != null ? session.getTopic().getId() : null)
                .topicTitle(session.getTopic() != null ? session.getTopic().getName() : null)
                .lessonCount(lessons.size())
                .lessons(lessonSummaries)
                .pronunciationAttemptCount(pronunciationAttemptRepository.countByLesson_Session_Id(id))
                .userLessonProgressCount(userLessonProgressRepository.countByLesson_Session_Id(id))
                .userSessionProgressCount(userSessionProgressRepository.countBySession_Id(id))
                .build();
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

    /**
     * Xóa session CHỈ KHI đã không còn dữ liệu liên quan.
     * Admin cần gọi API xóa từng loại trước: pronunciation-attempts, user-lesson-progress, user-session-progress.
     * Nếu còn bất kỳ bản ghi nào → ném ApiException với thông báo rõ ràng.
     */
    @Transactional
    public void delete(Long id) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found: " + id));

        long pronCount = pronunciationAttemptRepository.countByLesson_Session_Id(id);
        long progressCount = userLessonProgressRepository.countByLesson_Session_Id(id);
        long sessionProgressCount = userSessionProgressRepository.countBySession_Id(id);

        if (pronCount > 0 || progressCount > 0 || sessionProgressCount > 0) {
            StringBuilder msg = new StringBuilder(
                    "Không thể xóa session khi còn dữ liệu liên quan. Cần xóa trước qua API admin: ");
            if (pronCount > 0) msg.append(pronCount).append(" bản ghi phát âm; ");
            if (progressCount > 0) msg.append(progressCount).append(" tiến độ bài học; ");
            if (sessionProgressCount > 0) msg.append(sessionProgressCount).append(" tiến độ hoàn thành session. ");
            msg.append("Dùng DELETE /v1/admin/sessions/").append(id).append("/pronunciation-attempts, ");
            msg.append("/user-lesson-progress, /user-session-progress rồi thử xóa session lại.");
            throw new ApiException(msg.toString(), "SESSION_HAS_RELATED_DATA");
        }

        // Bỏ liên kết test với lesson để có thể xóa lesson (cascade)
        List<Lesson> lessons = lessonRepository.findBySessionIdOrderByOrderIndexAsc(id);
        for (Lesson lesson : lessons) {
            List<Test> tests = testRepository.findByLesson_IdOrderByIdAsc(lesson.getId());
            for (Test t : tests) {
                t.setLesson(null);
                testRepository.save(t);
            }
        }
        // Xóa session → cascade xóa lessons → cascade xóa lesson_videos, lesson_vocabularies
        sessionRepository.delete(session);
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

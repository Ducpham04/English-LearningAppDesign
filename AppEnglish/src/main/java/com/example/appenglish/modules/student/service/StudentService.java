package com.example.appenglish.modules.student.service;

import com.example.appenglish.entity.Lesson;
import com.example.appenglish.entity.User;
import com.example.appenglish.entity.UserLessonProgress;
import com.example.appenglish.entity.UserSessionProgress;
import com.example.appenglish.modules.auth.service.UserConverterService;
import com.example.appenglish.modules.curriculum.dto.CurriculumDto;
import com.example.appenglish.modules.curriculum.service.CurriculumService;
import com.example.appenglish.modules.lesson.service.LessonService;
import com.example.appenglish.modules.student.dto.SessionProgressDto;
import com.example.appenglish.modules.student.dto.StudentDashboardDto;
import com.example.appenglish.modules.userDetail.dto.UserDataDto;
import com.example.appenglish.modules.vocabulary.dto.AddToMyVocabularyRequest;
import com.example.appenglish.modules.vocabulary.dto.AddToMyVocabularyResponseDto;
import com.example.appenglish.modules.vocabulary.dto.MyVocabularyItemDto;
import com.example.appenglish.modules.vocabulary.service.VocabularyService;
import com.example.appenglish.repository.ClassLessonAssignmentRepository;
import com.example.appenglish.repository.ClassroomRepository;
import com.example.appenglish.repository.ClassStudentRepository;
import com.example.appenglish.repository.LessonRepository;
import com.example.appenglish.repository.SessionRepository;
import com.example.appenglish.repository.UserLessonProgressRepository;
import com.example.appenglish.repository.UserRepository;
import com.example.appenglish.repository.UserSessionProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final UserRepository userRepository;
    private final UserConverterService userConverterService;
    private final CurriculumService curriculumService;
    private final UserLessonProgressRepository userLessonProgressRepository;
    private final UserSessionProgressRepository userSessionProgressRepository;
    private final LessonRepository lessonRepository;
    private final SessionRepository sessionRepository;
    private final ClassStudentRepository classStudentRepository;
    private final ClassroomRepository classroomRepository;
    private final ClassLessonAssignmentRepository classLessonAssignmentRepository;
    private final LessonService lessonService;
    private final VocabularyService vocabularyService;

    public StudentDashboardDto getDashboard() {
        User user = getCurrentUser();
        UserDataDto userDto = userConverterService.convertUserToDataDto(user);
        List<CurriculumDto> courses = curriculumService.getAll();
        List<StudentDashboardDto.EnrolledClassDto> enrolledClasses = getEnrolledClassesList(user.getId());

        int xp = user.getXp() != null ? user.getXp() : 0;
        int level = user.getLevel() != null ? user.getLevel() : 1;
        int xpToNextLevel = Math.max(0, (level * 1000) - xp);

        return StudentDashboardDto.builder()
                .user(userDto)
                .courses(courses)
                .enrolledClasses(enrolledClasses)
                .stats(StudentDashboardDto.DashboardStatsDto.builder()
                        .totalXP(xp)
                        .currentLevel(level)
                        .xpToNextLevel(xpToNextLevel)
                        .streak(user.getStreak() != null ? user.getStreak() : 0)
                        .lessonsCompleted(user.getTotalLessonsCompleted() != null ? user.getTotalLessonsCompleted() : 0)
                        .wordsLearned(0)
                        .testsCompleted(0)
                        .badgesEarned(0)
                        .build())
                .build();
    }

    /** Lớp mà học sinh đã ghi danh (cho dashboard và GET /student/classes). */
    public List<StudentDashboardDto.EnrolledClassDto> getEnrolledClasses() {
        User user = getCurrentUser();
        return getEnrolledClassesList(user.getId());
    }

    private List<StudentDashboardDto.EnrolledClassDto> getEnrolledClassesList(String studentId) {
        List<StudentDashboardDto.EnrolledClassDto> out = new ArrayList<>();
        for (var cs : classStudentRepository.findByStudent_Id(studentId)) {
            var c = cs.getClassroom();
            if (c == null) continue;
            int assignmentCount = classLessonAssignmentRepository.findByClassroom_IdOrderByOrderIndexAscIdAsc(c.getId()).size();
            int studentCount = classStudentRepository.findByClassroom_Id(c.getId()).size();
            out.add(StudentDashboardDto.EnrolledClassDto.builder()
                    .id(c.getId())
                    .name(c.getName())
                    .teacherName(c.getTeacher() != null ? c.getTeacher().getFullName() : null)
                    .courseId(c.getCourse() != null ? c.getCourse().getId() : null)
                    .courseTitle(c.getCourse() != null ? c.getCourse().getTitle() : null)
                    .startDate(c.getStartDate() != null ? c.getStartDate().toString() : null)
                    .endDate(c.getEndDate() != null ? c.getEndDate().toString() : null)
                    .status(c.getStatus() != null ? c.getStatus().name() : null)
                    .studentCount(studentCount)
                    .assignmentCount(assignmentCount)
                    .build());
        }
        return out;
    }

    /** Bài tập giao trong lớp (chỉ cho lớp mà học sinh đã ghi danh). */
    public List<com.example.appenglish.modules.teacher.dto.AssignmentDto> getClassAssignments(Long classId) {
        User user = getCurrentUser();
        if (!classStudentRepository.existsByClassroom_IdAndStudent_Id(classId, user.getId())) {
            throw new RuntimeException("Not enrolled in this class");
        }
        return classLessonAssignmentRepository.findByClassroom_IdOrderByOrderIndexAscIdAsc(classId).stream()
                .map(a -> com.example.appenglish.modules.teacher.dto.AssignmentDto.builder()
                        .id(a.getId())
                        .classId(a.getClassroom().getId())
                        .lessonId(a.getLesson().getId())
                        .title(a.getTitle())
                        .description(a.getDescription())
                        .dueAt(a.getDueAt())
                        .maxScore(a.getMaxScore())
                        .isRequired(a.getIsRequired() != null ? a.getIsRequired() : false)
                        .orderIndex(a.getOrderIndex())
                        .lesson(lessonService.getById(a.getLesson().getId()))
                        .build())
                .collect(Collectors.toList());
    }

    public List<CurriculumDto> getCourses() {
        return curriculumService.getAll();
    }

    @Transactional
    public UserDataDto completLesson(Long lessonId, Integer score) {
        User user = getCurrentUser();
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found: " + lessonId));

        boolean alreadyCompleted = userLessonProgressRepository.existsByUser_IdAndLesson_Id(user.getId(), lessonId);
        if (!alreadyCompleted) {
            UserLessonProgress progress = new UserLessonProgress();
            progress.setUser(user);
            progress.setLesson(lesson);
            progress.setCompletedAt(LocalDateTime.now());
            progress.setScore(score);
            userLessonProgressRepository.save(progress);

            int xpEarned = score != null ? Math.max(1, score / 10) : 10;
            user.setXp((user.getXp() != null ? user.getXp() : 0) + xpEarned);
            user.setTotalLessonsCompleted((user.getTotalLessonsCompleted() != null ? user.getTotalLessonsCompleted() : 0) + 1);
            int newXp = user.getXp();
            int newLevel = Math.max(1, newXp / 1000 + 1);
            user.setLevel(newLevel);
            userRepository.save(user);

            if (lesson.getSession() != null) {
                Long sessionId = lesson.getSession().getId();
                long totalInSession = lessonRepository.findBySessionIdOrderByOrderIndexAsc(sessionId).size();
                long completedInSession = userLessonProgressRepository.countByUser_IdAndLesson_Session_Id(user.getId(), sessionId);
                if (totalInSession > 0 && completedInSession >= totalInSession
                        && !userSessionProgressRepository.existsByUser_IdAndSession_Id(user.getId(), sessionId)) {
                    UserSessionProgress sp = new UserSessionProgress();
                    sp.setUser(user);
                    sp.setSession(lesson.getSession());
                    sp.setCompletedAt(LocalDateTime.now());
                    userSessionProgressRepository.save(sp);
                }
            }
        }

        return userConverterService.convertUserToDataDto(user);
    }

    /** Danh sách ID bài học mà học sinh đã hoàn thành (để FE hiển thị tiến độ). */
    public List<Long> getCompletedLessonIds() {
        User user = getCurrentUser();
        return userLessonProgressRepository.findByUser_Id(user.getId()).stream()
                .map(p -> p.getLesson().getId())
                .collect(Collectors.toList());
    }

    /** Danh sách ID session mà học sinh đã hoàn thành. */
    public List<Long> getCompletedSessionIds() {
        User user = getCurrentUser();
        return userSessionProgressRepository.findByUser_Id(user.getId()).stream()
                .map(p -> p.getSession().getId())
                .collect(Collectors.toList());
    }

    /**
     * Các session mà học sinh đã/đang tham gia (có ít nhất 1 bài đã hoàn thành).
     * Dùng cho Dashboard "Đang học".
     */
    public List<SessionProgressDto> getParticipatedSessions() {
        User user = getCurrentUser();
        List<UserLessonProgress> progressList = userLessonProgressRepository.findByUser_Id(user.getId());
        List<Long> sessionIds = progressList.stream()
                .map(p -> p.getLesson().getSession())
                .filter(s -> s != null)
                .map(s -> s.getId())
                .distinct()
                .collect(Collectors.toList());

        return sessionIds.stream()
                .map(sessionId -> sessionRepository.findById(sessionId))
                .filter(opt -> opt.isPresent())
                .map(opt -> opt.get())
                .map(session -> {
                    int total = lessonRepository.findBySessionIdOrderByOrderIndexAsc(session.getId()).size();
                    int completed = (int) userLessonProgressRepository.countByUser_IdAndLesson_Session_Id(user.getId(), session.getId());
                    boolean sessionCompleted = userSessionProgressRepository.existsByUser_IdAndSession_Id(user.getId(), session.getId());
                    return SessionProgressDto.builder()
                            .sessionId(session.getId())
                            .title(session.getTitle())
                            .topicId(session.getTopic() != null ? session.getTopic().getId() : null)
                            .topicName(session.getTopic() != null ? session.getTopic().getName() : null)
                            .completedLessons(completed)
                            .totalLessons(total)
                            .sessionCompleted(sessionCompleted)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public AddToMyVocabularyResponseDto addToMyVocabulary(AddToMyVocabularyRequest request) {
        User user = getCurrentUser();
        return vocabularyService.addToMyVocabulary(user.getId(), request);
    }

    public List<MyVocabularyItemDto> getMyVocabulary(Long lessonId, Long sessionId) {
        User user = getCurrentUser();
        return vocabularyService.getMyVocabulary(user.getId(), lessonId, sessionId);
    }

    public void removeFromMyVocabulary(Long vocabularyId) {
        User user = getCurrentUser();
        vocabularyService.removeFromMyVocabulary(user.getId(), vocabularyId);
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }
}

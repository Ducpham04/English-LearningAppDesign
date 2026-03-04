package com.example.appenglish.modules.student.service;

import com.example.appenglish.entity.*;
import com.example.appenglish.modules.student.dto.*;
import com.example.appenglish.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final UserRepository userRepository;
    private final ClassroomRepository classroomRepository;
    private final CurriculumRepository curriculumRepository;
    private final SessionRepository sessionRepository;
    private final NotificationRepository notificationRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserVocabularyProgressRepository userVocabularyProgressRepository;
    private final XpRepository xpRepository;
    private final ClassStudentRepository classStudentRepository;

    /** Base XP for completing any lesson. Bonus from score (0–100) is added. */
    private static final int BASE_LESSON_XP = 50;
    private static final int MAX_BONUS_XP = 50;
    /** XP per level (level 2 at 250, level 3 at 500, ...). */
    private static final int XP_PER_LEVEL = 250;

    @Transactional
    public LessonCompleteResponse completeLesson(String email, Long lessonId, Integer score) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int bonusXp = (score != null && score >= 0 && score <= 100)
                ? (score * MAX_BONUS_XP / 100)
                : 0;
        int xpEarned = BASE_LESSON_XP + bonusXp;

        user.setTotalLessonsCompleted((user.getTotalLessonsCompleted() != null ? user.getTotalLessonsCompleted() : 0) + 1);
        user.setXp((user.getXp() != null ? user.getXp() : 0) + xpEarned);

        int newLevel = 1 + (user.getXp() / XP_PER_LEVEL);
        user.setLevel(newLevel);
        userRepository.save(user);

        XPLog xpLog = new XPLog();
        xpLog.setUser(user);
        xpLog.setAmount(xpEarned);
        xpLog.setAction("lesson_complete");
        xpRepository.save(xpLog);

        return LessonCompleteResponse.builder()
                .xpEarned(xpEarned)
                .level(user.getLevel())
                .xp(user.getXp())
                .totalLessonsCompleted(user.getTotalLessonsCompleted())
                .build();
    }

    @Transactional(readOnly = true)
    public StudentDashboardDto getStudentDashboardByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return getStudentDashboard(user.getId());
    }

    @Transactional(readOnly = true)
    public StudentDashboardDto getStudentDashboard(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get enrolled classes
        List<ClassInfoDto> classes = getEnrolledClasses(userId);

        // Get all available courses
        List<CourseInfoDto> courses = getAllCourses();

        // Get notifications
        List<NotificationDto> notifications = getNotifications(userId);

        // Get badges
        List<BadgeDto> badges = getBadges(userId);

        // Get stats
        DashboardStats stats = getDashboardStats(userId);

        return StudentDashboardDto.builder()
                .user(mapToUserInfo(user))
                .enrolledClasses(classes)
                .courses(courses)
                .notifications(notifications)
                .badges(badges)
                .stats(stats)
                .build();
    }

    private List<ClassInfoDto> getEnrolledClasses(String userId) {
        List<ClassStudent> classStudents = classStudentRepository.findByStudentId(userId);
        if (classStudents.isEmpty()) {
            return new ArrayList<>();
        }

        return classStudents.stream()
                .map(cs -> {
                    Classroom classroom = cs.getClassroom();
                    if (classroom == null) {
                        return null;
                    }

                    // Count students in this class
                    int studentCount = classStudentRepository.findByClassroomId(classroom.getId()).size();

                    return ClassInfoDto.builder()
                            .id(classroom.getId())
                            .name(classroom.getName())
                            .teacherName(classroom.getTeacher() != null ? classroom.getTeacher().getFullName() : null)
                            .teacherAvatar(classroom.getTeacher() != null ? classroom.getTeacher().getAvatarUrl() : null)
                            .startDate(classroom.getStartDate())
                            .endDate(classroom.getEndDate())
                            .status(classroom.getStatus() != null ? classroom.getStatus().name() : null)
                            .studentCount(studentCount)
                            .build();
                })
                .filter(c -> c != null)
                .collect(Collectors.toList());
    }

    public List<CourseInfoDto> getAllCourses() {
        return curriculumRepository.findAll().stream()
                .map(course -> {
                    int totalSessions = sessionRepository.findByCourseId(course.getId()).size();
                    return CourseInfoDto.builder()
                            .id(course.getId())
                            .title(course.getTitle())
                            .description(course.getDescription())
                            .thumbnailUrl(course.getThumbnailUrl())
                            .price(course.getPrice())
                            .teacherName(course.getTeacher() != null ? course.getTeacher().getFullName() : "Unknown")
                            .totalSessions(totalSessions)
                            .completedSessions(0) // TODO: Calculate from progress
                            .progressPercent(0)   // TODO: Calculate from progress
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<NotificationDto> getNotifications(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return new ArrayList<>();
        
        return notificationRepository.findByUser_Email(user.getEmail()).stream()
                .map(noti -> NotificationDto.builder()
                        .id(noti.getId())
                        .message(noti.getMessage())
                        .isRead(noti.getIsRead())
                        .type(noti.getMetadata() != null ? (String) noti.getMetadata().getOrDefault("type", "general") : "general")
                        .createdAt(noti.getCreatedAt() != null ? noti.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "")
                        .build())
                .collect(Collectors.toList());
    }

    private List<BadgeDto> getBadges(String userId) {
        List<Badge> allBadges = badgeRepository.findAll();
        List<Long> earnedBadgeIds = userBadgeRepository.findByUser_Id(userId).stream()
                .map(ub -> ub.getBadge().getId())
                .collect(Collectors.toList());

        return allBadges.stream()
                .map(badge -> {
                    boolean isEarned = earnedBadgeIds.contains(badge.getId());
                    return BadgeDto.builder()
                            .id(badge.getId())
                            .name(badge.getName())
                            .iconUrl(badge.getIconUrl())
                            .conditionRule(badge.getConditionRule())
                            .isEarned(isEarned)
                            .earnedAt(isEarned ? userBadgeRepository.findByUser_IdAndBadge_Id(userId, badge.getId())
                                    .map(ub -> ub.getEarnedAt() != null ? ub.getEarnedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                                    .orElse(null) : null)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private DashboardStats getDashboardStats(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        
        int wordsLearned = userVocabularyProgressRepository.findByUser_Id(userId).size();
        int badgesEarned = userBadgeRepository.findByUser_Id(userId).size();
        
        // Calculate XP to next level (each level requires 500 * level XP)
        int currentLevel = user != null ? user.getLevel() : 1;
        int currentXP = user != null ? user.getXp() : 0;
        int xpForNextLevel = 500 * currentLevel;
        int xpToNextLevel = xpForNextLevel - currentXP;

        return DashboardStats.builder()
                .totalXP(currentXP)
                .currentLevel(currentLevel)
                .xpToNextLevel(Math.max(0, xpToNextLevel))
                .streak(user != null ? user.getStreak() : 0)
                .lessonsCompleted(user != null ? user.getTotalLessonsCompleted() : 0)
                .wordsLearned(wordsLearned)
                .testsCompleted(0) // TODO: Calculate from test attempts
                .badgesEarned(badgesEarned)
                .build();
    }

    private int xpForCurrentLevel(int currentXP) {
        // XP required to reach current level (sum of 500 * level for each level)
        int total = 0;
        int level = 1;
        while (total + (500 * level) <= currentXP) {
            total += 500 * level;
            level++;
        }
        return total;
    }

    private UserInfoDto mapToUserInfo(User user) {
        return UserInfoDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .level(user.getLevel())
                .xp(user.getXp())
                .streak(user.getStreak())
                .totalLessonsCompleted(user.getTotalLessonsCompleted())
                .faceIdEnrolled(user.getFaceIdEnrolled())
                .build();
    }
}

package com.example.appenglish.modules.admin.service;

import com.example.appenglish.entity.Curriculum;
import com.example.appenglish.entity.UserLessonProgress;
import com.example.appenglish.modules.admin.dto.AdminCourseParticipantDto;
import com.example.appenglish.repository.CurriculumRepository;
import com.example.appenglish.repository.LessonRepository;
import com.example.appenglish.repository.UserLessonProgressRepository;
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
public class AdminCourseService {

    CurriculumRepository curriculumRepository;
    LessonRepository lessonRepository;
    UserLessonProgressRepository userLessonProgressRepository;

    @Transactional(readOnly = true)
    public List<AdminCourseParticipantDto> getParticipants(Long courseId) {
        Curriculum course = curriculumRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));

        List<UserLessonProgress> progress = userLessonProgressRepository.findByLesson_Course_Id(courseId);

        Map<String, List<UserLessonProgress>> byUser = progress.stream()
                .filter(p -> p.getUser() != null)
                .collect(Collectors.groupingBy(p -> p.getUser().getId()));

        int totalLessons = lessonRepository.findByCourseIdOrderByOrderIndexAsc(courseId).size();

        return byUser.entrySet().stream()
                .map(e -> {
                    List<UserLessonProgress> list = e.getValue();
                    if (list.isEmpty()) return null;
                    var u = list.get(0).getUser();

                    int completed = list.size();
                    boolean courseCompleted = (totalLessons > 0 && completed >= totalLessons);
                    LocalDateTime lastActivity = list.stream()
                            .map(UserLessonProgress::getCompletedAt)
                            .filter(Objects::nonNull)
                            .max(LocalDateTime::compareTo)
                            .orElse(null);

                    return AdminCourseParticipantDto.builder()
                            .userId(null)
                            .fullName(u.getFullName())
                            .email(u.getEmail())
                            .avatarUrl(u.getAvatarUrl())
                            .courseId(course.getId())
                            .courseTitle(course.getTitle())
                            .completedLessons(completed)
                            .totalLessons(totalLessons)
                            .courseCompleted(courseCompleted)
                            .lastActivityAt(lastActivity != null ? lastActivity.atZone(java.time.ZoneId.systemDefault()).toInstant() : null)
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeParticipant(Long courseId, String userId) {
        userLessonProgressRepository.deleteByUser_IdAndLesson_Course_Id(userId, courseId);
    }
}


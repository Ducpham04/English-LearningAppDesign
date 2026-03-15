package com.example.appenglish.modules.admin.service;

import com.example.appenglish.entity.Lesson;
import com.example.appenglish.entity.LessonVideo;
import com.example.appenglish.entity.PronunciationAttempt;
import com.example.appenglish.entity.User;
import com.example.appenglish.modules.admin.dto.*;
import com.example.appenglish.repository.LessonRepository;
import com.example.appenglish.repository.LessonVideoRepository;
import com.example.appenglish.repository.PronunciationAttemptRepository;
import com.example.appenglish.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service quản lý nội dung bài học: báo cáo video lessons, bản ghi phát âm, chấm điểm & phân tích AI.
 */
@Service
@RequiredArgsConstructor
public class AdminContentService {

    private final LessonRepository lessonRepository;
    private final LessonVideoRepository lessonVideoRepository;
    private final PronunciationAttemptRepository pronunciationAttemptRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public AdminContentReportDto getReport() {
        long totalLessons = lessonRepository.count();
        long lessonsWithVideo = lessonVideoRepository.count();
        long lessonsWithoutVideo = totalLessons - lessonsWithVideo;
        long totalAttempts = pronunciationAttemptRepository.count();
        long usersWithAttempts = pronunciationAttemptRepository.countDistinctUsers();

        return AdminContentReportDto.builder()
                .totalLessons(totalLessons)
                .lessonsWithVideo(lessonsWithVideo)
                .lessonsWithoutVideo(lessonsWithoutVideo)
                .totalPronunciationAttempts(totalAttempts)
                .totalUsersWithAttempts(usersWithAttempts)
                .build();
    }

    @Transactional(readOnly = true)
    public List<AdminVideoLessonDetailDto> getVideoLessonsDetail() {
        List<LessonVideo> videos = lessonVideoRepository.findAllWithLessonSessionTopic();
        List<AdminVideoLessonDetailDto> result = new ArrayList<>(videos.size());

        for (LessonVideo lv : videos) {
            Lesson l = lv.getLesson();
            if (l == null) continue;

            int segmentCount = 0;
            if (lv.getTranscriptJson() != null) {
                segmentCount = lv.getTranscriptJson().size();
            }

            result.add(AdminVideoLessonDetailDto.builder()
                    .lessonId(l.getId())
                    .lessonTitle(l.getTitle())
                    .lessonType(l.getType() != null ? l.getType().name() : null)
                    .lessonOrderIndex(l.getOrderIndex())
                    .sessionId(l.getSession() != null ? l.getSession().getId() : null)
                    .sessionTitle(l.getSession() != null ? l.getSession().getTitle() : null)
                    .topicId(l.getSession() != null && l.getSession().getTopic() != null ? l.getSession().getTopic().getId() : null)
                    .topicName(l.getSession() != null && l.getSession().getTopic() != null ? l.getSession().getTopic().getName() : null)
                    .videoUrl(lv.getVideoUrl())
                    .youtubeUrl(lv.getYoutubeUrl())
                    .transcriptSegmentCount(segmentCount)
                    .hasTranscript(segmentCount > 0)
                    .build());
        }

        result.sort(Comparator.comparing(AdminVideoLessonDetailDto::getTopicName, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(AdminVideoLessonDetailDto::getSessionTitle, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(AdminVideoLessonDetailDto::getLessonOrderIndex, Comparator.nullsLast(Comparator.naturalOrder())));

        return result;
    }

    @Transactional(readOnly = true)
    public Page<AdminPronunciationRecordDto> getPronunciationRecords(Long lessonId, String userId, Pageable pageable) {
        Page<PronunciationAttempt> page;
        if (lessonId != null) {
            page = pronunciationAttemptRepository.findByLesson_IdWithUserAndLessonOrderByCreatedAtDesc(lessonId, pageable);
        } else if (userId != null && !userId.isBlank()) {
            page = pronunciationAttemptRepository.findByUser_IdWithUserAndLessonOrderByCreatedAtDesc(userId, pageable);
        } else {
            page = pronunciationAttemptRepository.findAllWithUserAndLessonOrderByCreatedAtDesc(pageable);
        }

        return page.map(this::toPronunciationRecordDto);
    }

    @Transactional(readOnly = true)
    public List<AdminPronunciationUserSummaryDto> getPronunciationUserSummaries() {
        List<Object[]> rows = pronunciationAttemptRepository.findPronunciationStatsByUser();
        if (rows.isEmpty()) return Collections.emptyList();

        Set<String> userIds = rows.stream()
                .map(r -> (String) r[0])
                .collect(Collectors.toSet());
        List<User> users = userRepository.findAllById(userIds);
        Map<String, User> userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u));

        List<AdminPronunciationUserSummaryDto> result = new ArrayList<>(rows.size());
        for (Object[] r : rows) {
            String uid = (String) r[0];
            long count = ((Number) r[1]).longValue();
            Double avgScore = r[2] != null ? ((Number) r[2]).doubleValue() : null;
            java.time.LocalDateTime lastAt = (java.time.LocalDateTime) r[3];
            User u = userMap.get(uid);

            result.add(AdminPronunciationUserSummaryDto.builder()
                    .userId(uid)
                    .userEmail(u != null ? u.getEmail() : null)
                    .userFullName(u != null ? u.getFullName() : null)
                    .attemptCount(count)
                    .avgPronunciationScore(avgScore)
                    .lastAttemptAt(lastAt != null ? lastAt.atZone(ZoneId.systemDefault()).toInstant() : null)
                    .build());
        }

        result.sort(Comparator.comparing(AdminPronunciationUserSummaryDto::getLastAttemptAt,
                Comparator.nullsLast(Comparator.reverseOrder())));

        return result;
    }

    private AdminPronunciationRecordDto toPronunciationRecordDto(PronunciationAttempt p) {
        return AdminPronunciationRecordDto.builder()
                .id(p.getId())
                .createdAt(p.getCreatedAt() != null ? p.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant() : null)
                .userId(p.getUser() != null ? p.getUser().getId() : null)
                .userEmail(p.getUser() != null ? p.getUser().getEmail() : null)
                .userFullName(p.getUser() != null ? p.getUser().getFullName() : null)
                .lessonId(p.getLesson() != null ? p.getLesson().getId() : null)
                .lessonTitle(p.getLesson() != null ? p.getLesson().getTitle() : null)
                .sessionId(p.getLesson() != null && p.getLesson().getSession() != null ? p.getLesson().getSession().getId() : null)
                .sessionTitle(p.getLesson() != null && p.getLesson().getSession() != null ? p.getLesson().getSession().getTitle() : null)
                .expectedText(p.getExpectedText())
                .recognizedText(p.getRecognizedText())
                .pronunciationScore(p.getPronunciationScore())
                .accuracyScore(p.getAccuracyScore())
                .fluencyScore(p.getFluencyScore())
                .prosodyScore(p.getProsodyScore())
                .completenessScore(p.getCompletenessScore())
                .status(p.getStatus() != null ? p.getStatus().name() : null)
                .s3Status(p.getS3Status() != null ? p.getS3Status().name() : null)
                .errorMessage(p.getErrorMessage())
                .feedback(p.getFeedback())
                .build();
    }
}

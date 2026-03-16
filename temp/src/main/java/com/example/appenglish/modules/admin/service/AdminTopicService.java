package com.example.appenglish.modules.admin.service;

import com.example.appenglish.entity.Topic;
import com.example.appenglish.entity.UserLessonProgress;
import com.example.appenglish.modules.admin.dto.AdminTopicParticipantDto;
import com.example.appenglish.repository.LessonRepository;
import com.example.appenglish.repository.TopicRepository;
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
public class AdminTopicService {

    TopicRepository topicRepository;
    LessonRepository lessonRepository;
    UserLessonProgressRepository userLessonProgressRepository;

    @Transactional(readOnly = true)
    public List<AdminTopicParticipantDto> getParticipants(Long topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found: " + topicId));

        List<UserLessonProgress> progress = userLessonProgressRepository.findByLesson_Session_Topic_Id(topicId);

        Map<String, List<UserLessonProgress>> byUser = progress.stream()
                .filter(p -> p.getUser() != null)
                .collect(Collectors.groupingBy(p -> p.getUser().getId()));

        // Tổng số lesson thuộc topic (qua session)
        int totalLessons = lessonRepository.findAll().stream()
                .filter(l -> l.getSession() != null && l.getSession().getTopic() != null
                        && Objects.equals(l.getSession().getTopic().getId(), topicId))
                .toList()
                .size();

        return byUser.entrySet().stream()
                .map(e -> {
                    List<UserLessonProgress> list = e.getValue();
                    if (list.isEmpty()) return null;
                    var u = list.get(0).getUser();

                    int completed = list.size();
                    boolean topicCompleted = (totalLessons > 0 && completed >= totalLessons);
                    LocalDateTime lastActivity = list.stream()
                            .map(UserLessonProgress::getCompletedAt)
                            .filter(Objects::nonNull)
                            .max(LocalDateTime::compareTo)
                            .orElse(null);

                    return AdminTopicParticipantDto.builder()
                            .userId(null)
                            .fullName(u.getFullName())
                            .email(u.getEmail())
                            .avatarUrl(u.getAvatarUrl())
                            .topicId(topic.getId())
                            .topicName(topic.getName())
                            .completedLessons(completed)
                            .totalLessons(totalLessons)
                            .topicCompleted(topicCompleted)
                            .lastActivityAt(lastActivity != null ? lastActivity.atZone(java.time.ZoneId.systemDefault()).toInstant() : null)
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeParticipant(Long topicId, String userId) {
        userLessonProgressRepository.deleteByUser_IdAndLesson_Session_Topic_Id(userId, topicId);
    }
}


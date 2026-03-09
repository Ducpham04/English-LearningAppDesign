package com.example.appenglish.modules.topic.service;

import com.example.appenglish.entity.Session;
import com.example.appenglish.entity.Topic;
import com.example.appenglish.modules.session.dto.SessionCreateRequest;
import com.example.appenglish.modules.topic.dto.TopicCreateRequest;
import com.example.appenglish.modules.topic.dto.TopicDto;
import com.example.appenglish.repository.SessionRepository;
import com.example.appenglish.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final SessionRepository sessionRepository;

    /** Danh sách tất cả topic (admin) */
    public List<TopicDto> getAll() {
        return topicRepository.findAll().stream()
                .map(t -> toDto(t, true))
                .collect(Collectors.toList());
    }

    /** Danh sách topic đang hoạt động (student) - kể cả NULL isActive */
    public List<TopicDto> getActive() {
        return topicRepository.findActiveTopics().stream()
                .map(t -> toDto(t, true))
                .collect(Collectors.toList());
    }

    /** Chi tiết topic kèm danh sách sessions */
    public TopicDto getById(Long id) {
        return topicRepository.findById(id)
                .map(t -> toDto(t, true))
                .orElseThrow(() -> new RuntimeException("Topic not found: " + id));
    }

    public TopicDto create(TopicCreateRequest req) {
        Topic topic = Topic.builder()
                .name(req.getName())
                .description(req.getDescription())
                .difficultyLevel(req.getDifficultyLevel())
                .orderIndex(req.getOrderIndex() != null ? req.getOrderIndex() : 1)
                .estimatedDurationMinutes(req.getEstimatedDurationMinutes())
                .thumbnailUrl(req.getThumbnailUrl())
                .isActive(true)
                .build();
        return toDto(topicRepository.save(topic), false);
    }

    public TopicDto update(Long id, TopicCreateRequest req) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic not found: " + id));
        if (req.getName() != null) topic.setName(req.getName());
        if (req.getDescription() != null) topic.setDescription(req.getDescription());
        if (req.getDifficultyLevel() != null) topic.setDifficultyLevel(req.getDifficultyLevel());
        if (req.getOrderIndex() != null) topic.setOrderIndex(req.getOrderIndex());
        if (req.getEstimatedDurationMinutes() != null) topic.setEstimatedDurationMinutes(req.getEstimatedDurationMinutes());
        if (req.getThumbnailUrl() != null) topic.setThumbnailUrl(req.getThumbnailUrl());
        return toDto(topicRepository.save(topic), true);
    }

    public void toggleActive(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic not found: " + id));
        topic.setIsActive(!Boolean.TRUE.equals(topic.getIsActive()));
        topicRepository.save(topic);
    }

    public void delete(Long id) {
        topicRepository.deleteById(id);
    }

    /** Tạo session trong topic */
    public TopicDto.TopicSessionDto addSession(Long topicId, SessionCreateRequest req) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found: " + topicId));
        Session session = new Session();
        session.setTitle(req.getTitle());
        session.setTopic(topic);
        session.setOrderIndex(req.getOrderIndex() != null ? req.getOrderIndex() : 1);
        Session saved = sessionRepository.save(session);
        return toSessionDto(saved);
    }

    private TopicDto toDto(Topic t, boolean includeSessions) {
        List<Session> sessions = includeSessions
                ? sessionRepository.findByTopicIdOrderByOrderIndexAsc(t.getId())
                : List.of();

        return TopicDto.builder()
                .id(t.getId())
                .name(t.getName())
                .description(t.getDescription())
                .difficultyLevel(t.getDifficultyLevel())
                .orderIndex(t.getOrderIndex())
                .estimatedDurationMinutes(t.getEstimatedDurationMinutes())
                .thumbnailUrl(t.getThumbnailUrl())
                .isActive(t.getIsActive())
                .sessionCount(sessions.size())
                .sessions(sessions.stream().map(this::toSessionDto).collect(Collectors.toList()))
                .build();
    }

    private TopicDto.TopicSessionDto toSessionDto(Session s) {
        return TopicDto.TopicSessionDto.builder()
                .id(s.getId())
                .title(s.getTitle())
                .orderIndex(s.getOrderIndex())
                .lessonCount(s.getLessons() != null ? s.getLessons().size() : 0)
                .build();
    }
}

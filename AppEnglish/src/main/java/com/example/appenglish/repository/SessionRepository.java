package com.example.appenglish.repository;

import com.example.appenglish.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByCourseIdOrderByOrderIndexAsc(Long courseId);
    List<Session> findByTopicIdOrderByOrderIndexAsc(Long topicId);
}

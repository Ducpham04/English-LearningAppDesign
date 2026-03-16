package com.example.appenglish.repository;

import com.example.appenglish.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    List<Topic> findByIsActiveTrueOrderByOrderIndexAsc();

    /** Lấy topic đang hoạt động HOẶC chưa set (is_active NULL = coi là active) */
    @Query("SELECT t FROM Topic t WHERE t.isActive IS NULL OR t.isActive = true ORDER BY COALESCE(t.orderIndex, 9999) ASC")
    List<Topic> findActiveTopics();

    boolean existsByName(String name);
}

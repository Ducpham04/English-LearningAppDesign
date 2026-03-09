package com.example.appenglish.repository;

import com.example.appenglish.entity.UserLessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserLessonProgressRepository extends JpaRepository<UserLessonProgress, Long> {
    Optional<UserLessonProgress> findByUser_IdAndLesson_Id(String userId, Long lessonId);
    List<UserLessonProgress> findByUser_Id(String userId);
    boolean existsByUser_IdAndLesson_Id(String userId, Long lessonId);
    long countByUser_IdAndLesson_Session_Id(String userId, Long sessionId);
}

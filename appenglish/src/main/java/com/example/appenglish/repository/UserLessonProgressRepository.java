package com.example.appenglish.repository;

import com.example.appenglish.entity.UserLessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserLessonProgressRepository extends JpaRepository<UserLessonProgress, Long> {
    Optional<UserLessonProgress> findByUser_IdAndLesson_Id(String userId, Long lessonId);
    List<UserLessonProgress> findByLesson_Id(Long lessonId);
    List<UserLessonProgress> findByUser_Id(String userId);
    boolean existsByUser_IdAndLesson_Id(String userId, Long lessonId);
    long countByUser_IdAndLesson_Session_Id(String userId, Long sessionId);
    List<UserLessonProgress> findByLesson_Session_Id(Long sessionId);
    void deleteByUser_IdAndLesson_Session_Id(String userId, Long sessionId);
    List<UserLessonProgress> findByLesson_Course_Id(Long courseId);
    void deleteByUser_IdAndLesson_Course_Id(String userId, Long courseId);
    List<UserLessonProgress> findByLesson_Session_Topic_Id(Long topicId);
    void deleteByUser_IdAndLesson_Session_Topic_Id(String userId, Long topicId);
    void deleteByLesson_Session_Id(Long sessionId);
    long countByLesson_Session_Id(Long sessionId);
}

package com.example.appenglish.repository;

import com.example.appenglish.entity.Lesson;
import com.example.appenglish.enums.LessonType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByCourseId(Long courseId);
    List<Lesson> findBySessionId(Long sessionId);
    List<Lesson> findByType(LessonType type);
}

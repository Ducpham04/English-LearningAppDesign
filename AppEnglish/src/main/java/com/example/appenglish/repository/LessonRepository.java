package com.example.appenglish.repository;

import com.example.appenglish.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findBySessionIdOrderByOrderIndexAsc(Long sessionId);
    List<Lesson> findByCourseIdOrderByOrderIndexAsc(Long courseId);
    /** Lessons in sessions whose course is owned by this teacher (teacher id = User UUID). */
    List<Lesson> findBySession_Course_Teacher_IdOrderByOrderIndexAsc(String teacherId);
}


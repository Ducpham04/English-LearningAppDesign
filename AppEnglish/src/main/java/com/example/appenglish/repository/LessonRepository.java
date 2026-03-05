package com.example.AppEnglish.repository;

import com.example.AppEnglish.entity.Lesson;
import com.example.AppEnglish.enums.LessonType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    @Query("SELECT l FROM Lesson l LEFT JOIN FETCH l.video WHERE l.id = :id")
    Optional<Lesson> findByIdWithVideo(@Param("id") Long id);

    List<Lesson> findBySession_IdOrderByOrderIndexAsc(Long sessionId);

    long countBySession_Course_Id(Long courseId);

    @Query("SELECT l FROM Lesson l LEFT JOIN FETCH l.video WHERE l.type = :type ORDER BY l.orderIndex ASC")
    List<Lesson> findAllWithVideoByType(@Param("type") LessonType type);

    List<Lesson> findByTypeOrderByOrderIndexAsc(LessonType type);
}

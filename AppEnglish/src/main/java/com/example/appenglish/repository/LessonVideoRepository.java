package com.example.AppEnglish.repository;

import com.example.AppEnglish.entity.LessonVideo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LessonVideoRepository extends JpaRepository<LessonVideo, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select lv from LessonVideo lv where lv.lessonId = :lessonId")
    Optional<LessonVideo> findByLessonIdForUpdate(@Param("lessonId") Long lessonId);
}

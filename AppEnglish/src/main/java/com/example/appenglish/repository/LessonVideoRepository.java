package com.example.appenglish.repository;

import com.example.appenglish.entity.LessonVideo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LessonVideoRepository extends JpaRepository<LessonVideo, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select lv from LessonVideo lv where lv.lessonId = :lessonId")
    Optional<LessonVideo> findByLessonIdForUpdate(@Param("lessonId") Long lessonId);

    void deleteByLesson_Session_Id(Long sessionId);

    /** Lấy tất cả video lesson kèm lesson, session, topic để báo cáo. */
    @Query("select lv from LessonVideo lv join fetch lv.lesson l left join fetch l.session s left join fetch s.topic")
    List<LessonVideo> findAllWithLessonSessionTopic();
}

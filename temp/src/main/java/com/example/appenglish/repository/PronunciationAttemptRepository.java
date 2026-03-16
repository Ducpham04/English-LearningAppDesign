package com.example.appenglish.repository;

import com.example.appenglish.entity.PronunciationAttempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PronunciationAttemptRepository extends JpaRepository<PronunciationAttempt, Long> {
    void deleteByLesson_Session_Id(Long sessionId);
    long countByLesson_Id(Long lessonId);
    long countByLesson_Session_Id(Long sessionId);

    Page<PronunciationAttempt> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<PronunciationAttempt> findByLesson_IdOrderByCreatedAtDesc(Long lessonId, Pageable pageable);
    Page<PronunciationAttempt> findByUser_IdOrderByCreatedAtDesc(String userId, Pageable pageable);
    long countByUser_Id(String userId);

    @Query(value = "select p from PronunciationAttempt p left join fetch p.user left join fetch p.lesson l left join fetch l.session",
            countQuery = "select count(p) from PronunciationAttempt p")
    Page<PronunciationAttempt> findAllWithUserAndLessonOrderByCreatedAtDesc(Pageable pageable);

    @Query(value = "select p from PronunciationAttempt p left join fetch p.user left join fetch p.lesson l left join fetch l.session where p.lesson.id = :lessonId",
            countQuery = "select count(p) from PronunciationAttempt p where p.lesson.id = :lessonId")
    Page<PronunciationAttempt> findByLesson_IdWithUserAndLessonOrderByCreatedAtDesc(@Param("lessonId") Long lessonId, Pageable pageable);

    @Query(value = "select p from PronunciationAttempt p left join fetch p.user left join fetch p.lesson l left join fetch l.session where p.user.id = :userId",
            countQuery = "select count(p) from PronunciationAttempt p where p.user.id = :userId")
    Page<PronunciationAttempt> findByUser_IdWithUserAndLessonOrderByCreatedAtDesc(@Param("userId") String userId, Pageable pageable);

    @Query("select count(distinct p.user.id) from PronunciationAttempt p")
    long countDistinctUsers();

    @Query("select p.user.id, count(p), avg(p.pronunciationScore), max(p.createdAt) from PronunciationAttempt p group by p.user.id")
    List<Object[]> findPronunciationStatsByUser();
}


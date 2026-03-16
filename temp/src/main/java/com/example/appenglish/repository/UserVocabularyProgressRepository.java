package com.example.appenglish.repository;

import com.example.appenglish.entity.UserVocabId;
import com.example.appenglish.entity.UserVocabularyProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserVocabularyProgressRepository extends JpaRepository<UserVocabularyProgress, UserVocabId> {

    boolean existsByUser_IdAndVocabulary_Id(String userId, Long vocabularyId);

    @Query("SELECT uvp FROM UserVocabularyProgress uvp " +
           "JOIN FETCH uvp.vocabulary v " +
           "WHERE uvp.user.id = :userId " +
           "ORDER BY v.word")
    List<UserVocabularyProgress> findByUserIdWithVocabulary(@Param("userId") String userId);

    @Query("SELECT DISTINCT uvp FROM UserVocabularyProgress uvp " +
           "JOIN FETCH uvp.vocabulary v " +
           "JOIN v.lessonVocabularies lv " +
           "WHERE uvp.user.id = :userId AND lv.lesson.id = :lessonId " +
           "ORDER BY v.word")
    List<UserVocabularyProgress> findByUserIdAndLessonId(@Param("userId") String userId, @Param("lessonId") Long lessonId);

    @Query("SELECT DISTINCT uvp FROM UserVocabularyProgress uvp " +
           "JOIN FETCH uvp.vocabulary v " +
           "JOIN v.lessonVocabularies lv " +
           "JOIN lv.lesson l " +
           "WHERE uvp.user.id = :userId AND l.session.id = :sessionId " +
           "ORDER BY v.word")
    List<UserVocabularyProgress> findByUserIdAndSessionId(@Param("userId") String userId, @Param("sessionId") Long sessionId);
}

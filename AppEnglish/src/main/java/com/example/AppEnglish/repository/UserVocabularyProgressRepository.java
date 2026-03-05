package com.example.AppEnglish.repository;

import com.example.AppEnglish.entity.UserVocabularyProgress;
import com.example.AppEnglish.enums.LearningStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserVocabularyProgressRepository extends JpaRepository<UserVocabularyProgress, Long> {

    /**
     * Core SRS query — find all words due for review (nextReviewAt <= NOW)
     */
    @Query("""
                SELECT uvp FROM UserVocabularyProgress uvp
                JOIN FETCH uvp.vocabulary
                WHERE uvp.user.id = :userId
                  AND uvp.nextReviewAt <= :now
                ORDER BY uvp.nextReviewAt ASC
            """)
    List<UserVocabularyProgress> findDueForReview(
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now,
            Pageable pageable);

    /**
     * Count words by status for a user (for vocabulary stats)
     */
    long countByUser_IdAndStatus(Long userId, LearningStatus status);

    /**
     * Find progress for a specific user and vocabulary
     */
    @Query("""
                SELECT uvp FROM UserVocabularyProgress uvp
                WHERE uvp.user.id = :userId AND uvp.vocabulary.id = :vocabId
            """)
    java.util.Optional<UserVocabularyProgress> findByUserIdAndVocabId(
            @Param("userId") Long userId,
            @Param("vocabId") Long vocabId);
}

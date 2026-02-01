package com.example.appenglish.repository;

import com.example.appenglish.entity.UserVocabularyProgress;
import com.example.appenglish.entity.UserVocabId;
import com.example.appenglish.enums.LearningStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserVocabularyProgressRepository extends JpaRepository<UserVocabularyProgress, UserVocabId> {

    List<UserVocabularyProgress> findByUserId(Long userId);

    @Query("SELECT uvp FROM UserVocabularyProgress uvp WHERE uvp.user.id = :userId AND uvp.nextReviewAt <= :now ORDER BY uvp.nextReviewAt ASC")
    List<UserVocabularyProgress> findDueForReview(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    List<UserVocabularyProgress> findByUserIdAndStatus(Long userId, LearningStatus status);

    long countByUserIdAndStatus(Long userId, LearningStatus status);

    Optional<UserVocabularyProgress> findByUserIdAndVocabularyId(Long userId, Long vocabularyId);

    @Query("SELECT uvp FROM UserVocabularyProgress uvp WHERE uvp.nextReviewAt <= :now ORDER BY uvp.nextReviewAt ASC")
    List<UserVocabularyProgress> findAllDueForReview(@Param("now") LocalDateTime now);

    @Query("SELECT uvp FROM UserVocabularyProgress uvp WHERE uvp.user.id = :userId AND uvp.status = :status ORDER BY uvp.nextReviewAt ASC")
    List<UserVocabularyProgress> findByUserIdAndStatusOrderByNextReview(@Param("userId") Long userId, @Param("status") LearningStatus status);
}
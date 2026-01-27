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

/**
 * Repository cho UserVocabularyProgress (Tiến độ học từ vựng).
 */
@Repository
public interface UserVocabularyProgressRepository extends JpaRepository<UserVocabularyProgress, UserVocabId> {

    /**
     * Tìm tất cả từ vựng đang học của người dùng.
     */
    List<UserVocabularyProgress> findByUserId(Long userId);

    /**
     * Tìm các từ vựng cần ôn tập (nextReviewAt <= now).
     */
    @Query("SELECT uvp FROM UserVocabularyProgress uvp WHERE uvp.user.id = :userId AND uvp.nextReviewAt <= :now ORDER BY uvp.nextReviewAt ASC")
    List<UserVocabularyProgress> findDueForReview(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    /**
     * Tìm các từ vựng mới chưa học.
     */
    List<UserVocabularyProgress> findByUserIdAndStatus(Long userId, LearningStatus status);

    /**
     * Đếm số từ vựng đã thành thạo.
     */
    long countByUserIdAndStatus(Long userId, LearningStatus status);

    /**
     * Tìm từ vựng cụ thể của người dùng.
     */
    Optional<UserVocabularyProgress> findByUserIdAndVocabularyId(Long userId, Long vocabularyId);

    /**
     * Kiểm tra người dùng đã học từ vựng này hay chưa.
     */
    boolean existsByUserIdAndVocabularyId(Long userId, Long vocabularyId);

    /**
     * Tìm các từ vựng sắp được ôn tập.
     */
    @Query("SELECT uvp FROM UserVocabularyProgress uvp WHERE uvp.user.id = :userId AND uvp.nextReviewAt > :now ORDER BY uvp.nextReviewAt ASC LIMIT :limit")
    List<UserVocabularyProgress> findUpcomingReviews(@Param("userId") Long userId, @Param("now") LocalDateTime now, @Param("limit") int limit);

    /**
     * Tìm các từ vựng theo trạng thái, sắp xếp theo ngày ôn tiếp.
     */
    @Query("SELECT uvp FROM UserVocabularyProgress uvp WHERE uvp.user.id = :userId AND uvp.status = :status ORDER BY uvp.nextReviewAt ASC")
    List<UserVocabularyProgress> findByUserIdAndStatusOrderByNextReview(@Param("userId") Long userId, @Param("status") LearningStatus status);
}

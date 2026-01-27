package com.example.appenglish.repository;

import com.example.appenglish.entity.Lesson;
import com.example.appenglish.enums.LessonType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho Lesson (Bài học).
 */
@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    /**
     * Tìm tất cả bài học của một khóa học, sắp xếp theo thứ tự.
     */
    List<Lesson> findByCourseIdOrderByOrderIndexAsc(Long courseId);

    /**
     * Tìm tất cả bài học của một chương, sắp xếp theo thứ tự.
     */
    @Query("SELECT l FROM Lesson l WHERE l.session.id = :sessionId ORDER BY l.orderIndex ASC")
    List<Lesson> findBySessionIdOrderByOrderIndexAsc(@Param("sessionId") Long sessionId);

    /**
     * Tìm bài học theo loại.
     */
    List<Lesson> findByType(LessonType type);

    /**
     * Tìm bài học theo loại và khóa học.
     */
    List<Lesson> findByCourseIdAndType(Long courseId, LessonType type);

    /**
     * Tìm bài học theo tiêu đề.
     */
    List<Lesson> findByTitleContainingIgnoreCase(String title);

    /**
     * Đếm số bài học trong một chương.
     */
    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.session.id = :sessionId")
    long countBySessionId(@Param("sessionId") Long sessionId);

    /**
     * Đếm số bài học trong một khóa học.
     */
    long countByCourseId(Long courseId);

    /**
     * Tìm thứ tự bài học cao nhất trong một chương.
     */
    @Query("SELECT MAX(l.orderIndex) FROM Lesson l WHERE l.session.id = :sessionId")
    Integer findMaxOrderIndexBySessionId(@Param("sessionId") Long sessionId);

    /**
     * Kiểm tra bài học có tồn tại trong khóa học.
     */
    boolean existsByIdAndCourseId(Long id, Long courseId);

    /**
     * Tìm bài học theo ID chương và thứ tự.
     */
    @Query("SELECT l FROM Lesson l WHERE l.session.id = :sessionId AND l.orderIndex = :orderIndex")
    Optional<Lesson> findBySessionIdAndOrderIndex(@Param("sessionId") Long sessionId, @Param("orderIndex") Integer orderIndex);
}

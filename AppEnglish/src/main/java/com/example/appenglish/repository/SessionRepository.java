package com.example.appenglish.repository;

import com.example.appenglish.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho Session (Chương học).
 */
@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    /**
     * Tìm tất cả chương của một khóa học, sắp xếp theo thứ tự.
     */
    List<Session> findByCourseIdOrderByOrderIndexAsc(Long courseId);

    /**
     * Tìm chương theo ID khóa học và thứ tự.
     */
    Optional<Session> findByCourseIdAndOrderIndex(Long courseId, Integer orderIndex);

    /**
     * Tìm chương theo tiêu đề.
     */
    List<Session> findByTitleContainingIgnoreCase(String title);

    /**
     * Tìm tất cả chương của một khóa học.
     */
    List<Session> findByCourseId(Long courseId);

    /**
     * Đếm số chương trong một khóa học.
     */
    long countByCourseId(Long courseId);

    /**
     * Tìm thứ tự chương cao nhất trong một khóa học.
     */
    @Query("SELECT MAX(s.orderIndex) FROM Session s WHERE s.course.id = :courseId")
    Integer findMaxOrderIndexByCourseId(@Param("courseId") Long courseId);

    /**
     * Kiểm tra chương có tồn tại trong khóa học.
     */
    boolean existsByIdAndCourseId(Long id, Long courseId);

    
}

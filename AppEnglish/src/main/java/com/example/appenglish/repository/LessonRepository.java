package com.example.appenglish.repository;

import com.example.appenglish.entity.Lesson;
import com.example.appenglish.enums.LessonType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findByCourseIdOrderByOrderIndexAsc(Long courseId);

    @Query("SELECT l FROM Lesson l WHERE l.session.id = :sessionId ORDER BY l.orderIndex ASC")
    List<Lesson> findBySessionIdOrderByOrderIndexAsc(@Param("sessionId") Long sessionId);

    List<Lesson> findByType(LessonType type);

    List<Lesson> findByCourseIdAndType(Long courseId, LessonType type);

    List<Lesson> findByTitleContainingIgnoreCase(String title);

    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.session.id = :sessionId")
    long countBySessionId(@Param("sessionId") Long sessionId);

    long countByCourseId(Long courseId);

    @Query("SELECT MAX(l.orderIndex) FROM Lesson l WHERE l.session.id = :sessionId")
    Integer findMaxOrderIndexBySessionId(@Param("sessionId") Long sessionId);

    boolean existsByIdAndCourseId(Long id, Long courseId);

    @Query("SELECT l FROM Lesson l WHERE l.session.id = :sessionId AND l.orderIndex = :orderIndex")
    Optional<Lesson> findBySessionIdAndOrderIndex(@Param("sessionId") Long sessionId, @Param("orderIndex") Integer orderIndex);

    /**
     * FIX LỖI: Tìm bài học kế tiếp trong cùng một Session dựa trên thứ tự bài học
     */
    Optional<Lesson> findFirstBySessionIdAndOrderIndexGreaterThanOrderByOrderIndexAsc(Long sessionId, Integer orderIndex);

    // --- Native Queries cho Dashboard/Progress ---

    @Query(value = "SELECT COUNT(l.id) FROM lessons l " +
            "JOIN sessions s ON l.session_id = s.id " +
            "JOIN classrooms cl ON s.course_id = cl.course_id " + // Đã sửa tên bảng/cột theo cấu trúc entity
            "WHERE cl.id = :classroomId", nativeQuery = true)
    long countByClassroomId(@Param("classroomId") Long classroomId);

    @Query(value = "SELECT COUNT(ulp.id) FROM user_lesson_progress ulp " +
            "JOIN lessons l ON ulp.lesson_id = l.id " +
            "JOIN sessions s ON l.session_id = s.id " +
            "JOIN classrooms cl ON s.course_id = cl.course_id " +
            "WHERE cl.id = :classroomId " +
            "AND ulp.user_id = :userId " +
            "AND ulp.status = 'COMPLETED'", nativeQuery = true)
    long countCompletedByUserIdAndClassroomId(@Param("userId") Long userId, @Param("classroomId") Long classroomId);
}
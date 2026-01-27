package com.example.appenglish.repository;

import com.example.appenglish.entity.Classroom;
import com.example.appenglish.enums.ClassStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository cho Classroom (Lớp học).
 */
@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {

    /**
     * Tìm tất cả lớp học của một giáo viên.
     */
    List<Classroom> findByTeacherId(Long teacherId);

    /**
     * Tìm tất cả lớp học theo trạng thái.
     */
    List<Classroom> findByStatus(ClassStatus status);

    /**
     * Tìm các lớp học đang hoạt động của một giáo viên.
     */
    @Query("SELECT c FROM Classroom c WHERE c.teacher.id = :teacherId AND c.status = 'ACTIVE'")
    List<Classroom> findActiveClassroomsByTeacher(@Param("teacherId") Long teacherId);

    /**
     * Tìm lớp học theo tên.
     */
    List<Classroom> findByNameContainingIgnoreCase(String name);

    /**
     * Tìm các lớp học sắp bắt đầu.
     */
    @Query("SELECT c FROM Classroom c WHERE c.startDate > :today AND c.status = 'ACTIVE'")
    List<Classroom> findUpcomingClassrooms(@Param("today") LocalDate today);

    /**
     * Tìm các lớp học đang diễn ra.
     */
    @Query("SELECT c FROM Classroom c WHERE c.startDate <= :today AND c.endDate >= :today AND c.status = 'ACTIVE'")
    List<Classroom> findOngoingClassrooms(@Param("today") LocalDate today);

    /**
     * Tìm lớp học theo giáo viên và tên.
     */
    Optional<Classroom> findByTeacherIdAndName(Long teacherId, String name);

    /**
     * Kiểm tra lớp học có tồn tại.
     */
    boolean existsByIdAndTeacherId(Long id, Long teacherId);
}

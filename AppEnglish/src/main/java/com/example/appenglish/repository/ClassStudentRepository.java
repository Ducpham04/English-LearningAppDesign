package com.example.appenglish.repository;

import com.example.appenglish.entity.ClassStudent;
import com.example.appenglish.entity.ClassStudentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository cho ClassStudent entity.
 */
@Repository
public interface ClassStudentRepository extends JpaRepository<ClassStudent, ClassStudentId> {

    /**
     * Đếm tổng số học viên trong lớp.
     */
    @Query("SELECT COUNT(cs) FROM ClassStudent cs WHERE cs.classroom.id = :classroomId")
    long countByClassroomId(@Param("classroomId") Long classroomId);

    /**
     * Đếm số học viên theo lớp và trạng thái.
     */
    @Query("SELECT COUNT(cs) FROM ClassStudent cs WHERE cs.classroom.id = :classroomId AND cs.status = :status")
    long countByClassroomIdAndStatus(@Param("classroomId") Long classroomId, @Param("status") String status);

    /**
     * Tìm ClassStudent theo lớp và học viên.
     */
    @Query("SELECT cs FROM ClassStudent cs WHERE cs.classroom.id = :classroomId AND cs.student.id = :studentId")
    Optional<ClassStudent> findByClassroomIdAndUserId(@Param("classroomId") Long classroomId, @Param("studentId") Long studentId);
}

package com.example.appenglish.repository;

import com.example.appenglish.entity.ClassStudent;
import com.example.appenglish.entity.ClassStudentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassStudentRepository extends JpaRepository<ClassStudent, ClassStudentId> {
    @Query("SELECT cs FROM ClassStudent cs WHERE cs.classroom.id = :classroomId")
    List<ClassStudent> findByClassroomId(Long classroomId);
    
    @Query("SELECT cs FROM ClassStudent cs WHERE cs.student.id = :studentId")
    List<ClassStudent> findByStudentId(String studentId);
}

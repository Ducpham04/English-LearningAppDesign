package com.example.appenglish.repository;

import com.example.appenglish.entity.ClassStudent;
import com.example.appenglish.entity.ClassStudentId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassStudentRepository extends JpaRepository<ClassStudent, ClassStudentId> {
    List<ClassStudent> findByClassroom_Id(Long classId);
    List<ClassStudent> findByStudent_Id(String studentId);
    Optional<ClassStudent> findByClassroom_IdAndStudent_Id(Long classId, String studentId);
    boolean existsByClassroom_IdAndStudent_Id(Long classId, String studentId);
    void deleteByClassroom_IdAndStudent_Id(Long classId, String studentId);
}

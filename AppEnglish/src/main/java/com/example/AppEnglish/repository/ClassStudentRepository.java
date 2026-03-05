package com.example.AppEnglish.repository;

import com.example.AppEnglish.entity.ClassStudent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassStudentRepository extends JpaRepository<ClassStudent, Long> {

    List<ClassStudent> findByClassroom_Id(Long classId);

    long countByClassroom_Id(Long classId);

    boolean existsByClassroom_IdAndStudent_Id(Long classId, Long studentId);
}

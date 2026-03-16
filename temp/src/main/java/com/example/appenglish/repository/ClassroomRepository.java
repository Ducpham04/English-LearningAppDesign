package com.example.appenglish.repository;

import com.example.appenglish.entity.Classroom;
import com.example.appenglish.enums.ClassStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    List<Classroom> findByTeacher_Id(String teacherId);
    List<Classroom> findByCourse_Id(Long courseId);
    boolean existsByNameAndTeacher_Id(String name, String teacherId);
    Optional<Classroom> findByJoinCode(String joinCode);
    boolean existsByJoinCode(String joinCode);
}

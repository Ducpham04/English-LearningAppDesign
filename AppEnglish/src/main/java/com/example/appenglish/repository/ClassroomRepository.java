package com.example.appenglish.repository;

import com.example.appenglish.entity.Classroom;
import com.example.appenglish.enums.ClassStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    List<Classroom> findByTeacher_Id(String teacherId);
    List<Classroom> findByCourse_Id(Long courseId);
    boolean existsByNameAndTeacher_Id(String name, String teacherId);
}

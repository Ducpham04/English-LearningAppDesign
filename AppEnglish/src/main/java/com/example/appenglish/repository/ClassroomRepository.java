package com.example.appenglish.repository;

import com.example.appenglish.enums.ClassStatus;
import com.example.appenglish.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    @Query("SELECT c FROM Classroom c WHERE c.teacher.id = :teacherId")
    List<Classroom> findByTeacherId(String teacherId);
    List<Classroom> findByStatus(ClassStatus status);
}

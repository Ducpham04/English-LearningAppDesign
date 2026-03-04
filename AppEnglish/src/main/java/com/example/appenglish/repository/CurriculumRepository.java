package com.example.appenglish.repository;

import com.example.appenglish.entity.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {
    @Query("SELECT c FROM Curriculum c WHERE c.teacher.id = :teacherId")
    List<Curriculum> findByTeacherId(String teacherId);
}

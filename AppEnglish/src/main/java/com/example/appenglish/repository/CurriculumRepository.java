package com.example.appenglish.repository;

import com.example.appenglish.entity.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {
    List<Curriculum> findByTeacherId(String teacherId);
}

package com.example.appenglish.repository;

import com.example.appenglish.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestRepository extends JpaRepository<Test, Long> {
    List<Test> findByCourse_IdOrderByIdAsc(Long courseId);
    List<Test> findByLesson_IdOrderByIdAsc(Long lessonId);
}

package com.example.appenglish.repository;

import com.example.appenglish.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    List<Test> findByCourseId(Long courseId);
    List<Test> findByLessonId(Long lessonId);
}

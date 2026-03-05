package com.example.AppEnglish.repository;

import com.example.AppEnglish.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TestRepository extends JpaRepository<Test, Long> {

    Optional<Test> findByLesson_Id(Long lessonId);
}

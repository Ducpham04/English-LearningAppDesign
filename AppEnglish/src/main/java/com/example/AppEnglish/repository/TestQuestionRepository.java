package com.example.AppEnglish.repository;

import com.example.AppEnglish.entity.TestQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestQuestionRepository extends JpaRepository<TestQuestion, Long> {

    List<TestQuestion> findByTest_IdOrderByIdAsc(Long testId);
}

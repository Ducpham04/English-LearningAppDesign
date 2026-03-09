package com.example.appenglish.repository;

import com.example.appenglish.entity.TestQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestQuestionRepository extends JpaRepository<TestQuestion, Long> {
    List<TestQuestion> findByTest_IdOrderByOrderIndexAscIdAsc(Long testId);
}

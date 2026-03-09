package com.example.appenglish.repository;

import com.example.appenglish.entity.TestAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestAnswerRepository extends JpaRepository<TestAnswer, Long> {
    List<TestAnswer> findByAttempt_IdOrderByIdAsc(Long attemptId);
}

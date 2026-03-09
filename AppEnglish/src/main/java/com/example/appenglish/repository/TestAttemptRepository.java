package com.example.appenglish.repository;

import com.example.appenglish.entity.TestAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TestAttemptRepository extends JpaRepository<TestAttempt, Long> {
    List<TestAttempt> findByTest_IdAndUser_IdOrderBySubmittedAtDesc(Long testId, String userId);
    int countByTest_IdAndUser_Id(Long testId, String userId);
    Optional<TestAttempt> findFirstByTest_IdAndUser_IdOrderByTotalScoreDesc(Long testId, String userId);
}

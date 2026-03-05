package com.example.AppEnglish.repository;

import com.example.AppEnglish.entity.PronunciationAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PronunciationAttemptRepository extends JpaRepository<PronunciationAttempt, Long> {
}


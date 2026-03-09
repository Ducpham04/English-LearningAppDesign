package com.example.appenglish.repository;

import com.example.appenglish.entity.UserSessionProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSessionProgressRepository extends JpaRepository<UserSessionProgress, Long> {
    Optional<UserSessionProgress> findByUser_IdAndSession_Id(String userId, Long sessionId);
    List<UserSessionProgress> findByUser_Id(String userId);
    boolean existsByUser_IdAndSession_Id(String userId, Long sessionId);
}

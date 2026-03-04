package com.example.appenglish.repository;

import com.example.appenglish.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    List<UserBadge> findByUser_Id(String userId);
    Optional<UserBadge> findByUser_IdAndBadge_Id(String userId, Long badgeId);
}

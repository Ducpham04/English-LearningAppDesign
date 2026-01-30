package com.example.appenglish.repository;

import com.example.appenglish.entity.UserBadge;
import com.example.appenglish.entity.UserBadgeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBadgeRepository extends JpaRepository<UserBadge, UserBadgeId> {
}

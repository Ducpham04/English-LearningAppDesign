package com.example.appenglish.repository;

import com.example.appenglish.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
    Badge findByCriteria(String criteria); // Tìm badge dựa trên điều kiện
}

package com.example.appenglish.repository;

import com.example.appenglish.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
    @Query("SELECT b FROM Badge b WHERE b.criteria = :criteria")
    Optional<Badge> findByCriteria(@Param("criteria") String criteria);
}

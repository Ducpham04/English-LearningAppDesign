package com.example.appenglish.repository;

import com.example.appenglish.entity.XPLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface XPLogRepository extends JpaRepository<XPLog, Long> {
    @Query("SELECT x FROM XPLog x WHERE x.user.id = :userId")
    List<XPLog> findByUserId(Long userId);
}

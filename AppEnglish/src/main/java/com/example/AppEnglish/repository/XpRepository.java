package com.example.appenglish.repository;

import com.example.appenglish.entity.XPLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XpRepository extends JpaRepository<XPLog,Long> {
}

package com.example.appenglish.repository;

import com.example.appenglish.entity.TestAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestAttemptRepository extends JpaRepository<TestAttempt, Long> {
    // Interface này giúp Spring Boot tự động tạo các hàm lưu dữ liệu (save, find,...)
}
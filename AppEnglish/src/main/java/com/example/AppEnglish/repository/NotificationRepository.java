package com.example.appenglish.repository;

import com.example.appenglish.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Tuỳ chỉnh query nếu cần (ví dụ tìm thông báo theo user)
}

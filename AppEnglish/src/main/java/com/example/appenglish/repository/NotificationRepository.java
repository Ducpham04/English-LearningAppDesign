package com.example.appenglish.repository;

import com.example.appenglish.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(String userId);
    List<Notification> findByUser_Email(String email);
    List<Notification> findByUserIdAndIsRead(String userId, Boolean isRead);
}

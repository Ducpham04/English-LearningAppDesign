package com.example.appenglish.repository;

import com.example.appenglish.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    //Tìm thông báo theo userId
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId")
    List<Notification> findNotificationsByUserId(Long userId);

    //Tìm thông báo chưa đọc (isRead = false) thuộc về một user (userId).
    @Query("SELECT n FROM Notification n WHERE n.isRead = false AND n.user.id = :userId")
    List<Notification> findUnreadNotificationsByUserId(Long userId);

    // Đánh dấu thông báo đã đọc
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :id")
    void markNotificationAsRead(Long id);

}

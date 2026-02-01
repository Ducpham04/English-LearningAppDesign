package com.example.appenglish.service;

import com.example.appenglish.entity.Notification;
import com.example.appenglish.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    /**
     * Lấy danh sách thông báo cho user dựa trên userId
     */
    public List<Notification> getNotificationsByUserId(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);

        if (notifications.isEmpty()) {
            System.out.println("No notifications found for user with ID: " + userId);
        }

        return notifications; // Trả về danh sách, nếu rỗng sẽ trả `[]`
    }
}

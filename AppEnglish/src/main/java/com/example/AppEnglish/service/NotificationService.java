package com.example.appenglish.service;

import com.example.appenglish.entity.Notification;
import com.example.appenglish.entity.User;
import com.example.appenglish.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    /**
     * Lưu thông báo vào DB và gửi qua WebSocket
     */
    public void sendToUser(User user, String message, Map<String, Object> metadata) {
        // Thêm timestamp nếu chưa có
        metadata.putIfAbsent("timestamp", LocalDateTime.now().toString());
        metadata.putIfAbsent("type", "INFO");

        // Lưu vào DB
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setMetadata(metadata);
        notificationRepository.save(notification);

        // Gửi qua WebSocket
        try {
            simpMessagingTemplate.convertAndSendToUser(
                    String.valueOf(user.getId()),
                    "/queue/notifications",
                    Map.of(
                            "message", message,
                            "metadata", metadata,
                            "timestamp", metadata.get("timestamp")
                    )
            );
        } catch (Exception e) {
            System.err.println("Failed to send WebSocket notification: " + e.getMessage());
        }
    }

    /**
     * Lấy danh sách thông báo cho user dựa trên userId
     */
    public List<Notification> getNotificationsByUserId(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);

        if (notifications.isEmpty()) {
            System.out.println("No notifications found for user with ID: " + userId);
        }

        return notifications;
    }
}
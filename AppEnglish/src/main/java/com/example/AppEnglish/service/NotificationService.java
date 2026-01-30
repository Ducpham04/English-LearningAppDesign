package com.example.appenglish.service;

import com.example.appenglish.entity.Notification;
import com.example.appenglish.repository.NotificationRepository;
import com.example.appenglish.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, SimpMessagingTemplate simpMessagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void sendToUser(User user, String message, Map<String, Object> metadata) {
        // Tạo và lưu thông báo
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setMetadata(metadata); // Lưu JSON vào metadata
        notificationRepository.save(notification);

        // Gửi qua WebSocket
        simpMessagingTemplate.convertAndSendToUser(
                String.valueOf(user.getId()),
                "/queue/notifications",
                Map.of("message", message, "metadata", metadata)
        );
    }
}
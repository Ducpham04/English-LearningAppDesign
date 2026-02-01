package com.example.appenglish.modules.auth.controller;

import com.example.appenglish.entity.Notification;

import com.example.appenglish.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getUserNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);

        // Serialize chỉ trường message và metadata
        List<Map<String, Object>> response = notifications.stream().map(notification -> Map.of(
                "id", notification.getId(),
                "message", notification.getMessage(),
                "metadata", notification.getMetadata(),
                "isRead", notification.getIsRead()
        )).toList();

        return ResponseEntity.ok(response);
    }
}

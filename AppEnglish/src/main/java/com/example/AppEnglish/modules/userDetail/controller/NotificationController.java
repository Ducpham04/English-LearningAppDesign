package com.example.appenglish.modules.userDetail.controller;

import com.example.appenglish.entity.User;
import com.example.appenglish.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send/{userId}")
    public ResponseEntity<String> sendNotification(
            @PathVariable Long userId,
            @RequestParam String message,
            @RequestBody Map<String, Object> metadata
    ) {
        // Tạo user tạm thời chỉ để demo, thực tế sẽ lấy từ DB
        User user = new User();
        user.setId(userId);

        notificationService.sendToUser(user, message, metadata);
        return ResponseEntity.ok("Notification sent!");
    }
}

package com.example.appenglish.modules.userDetail.controller;

import com.example.appenglish.entity.User;
import com.example.appenglish.modules.userDetail.dto.NotificationDto;
import com.example.appenglish.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationDto notificationDTO) {
        // Tạo user từ userId trong DTO
        User user = new User();
        user.setId(notificationDTO.getUserId());

        // Gọi service để gửi thông báo
        notificationService.sendToUser(user, notificationDTO.getMessage(), notificationDTO.getMetadata());
        return ResponseEntity.ok("Notification sent successfully!");
    }
}

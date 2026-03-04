package com.example.appenglish.modules.notification.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.modules.notification.dto.NotificationDto;
import com.example.appenglish.modules.notification.dto.NotificationRequest;
import com.example.appenglish.modules.notification.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<BaseResponse<List<NotificationDto>>> getNotificationsByUser(
            @PathVariable String userId,
            HttpServletRequest httpReq) {
        List<NotificationDto> notifications = notificationService.getNotificationsByUser(userId);
        return ResponseEntity.ok(
                BaseResponse.ok(notifications, "Get notifications successfully", httpReq.getRequestURI())
        );
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<BaseResponse<List<NotificationDto>>> getUnreadNotifications(
            @PathVariable String userId,
            HttpServletRequest httpReq) {
        List<NotificationDto> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(
                BaseResponse.ok(notifications, "Get unread notifications successfully", httpReq.getRequestURI())
        );
    }

    @PostMapping
    public ResponseEntity<BaseResponse<NotificationDto>> createNotification(
            @Valid @RequestBody NotificationRequest request,
            HttpServletRequest httpReq) {
        NotificationDto notification = notificationService.createNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.ok(notification, "Notification created successfully", httpReq.getRequestURI()));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<BaseResponse<NotificationDto>> markAsRead(
            @PathVariable Long id,
            HttpServletRequest httpReq) {
        NotificationDto notification = notificationService.markAsRead(id);
        return ResponseEntity.ok(
                BaseResponse.ok(notification, "Notification marked as read", httpReq.getRequestURI())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<?>> deleteNotification(
            @PathVariable Long id,
            HttpServletRequest httpReq) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok(
                BaseResponse.ok(null, "Notification deleted successfully", httpReq.getRequestURI())
        );
    }
}

package com.example.appenglish.modules.notification.service;

import com.example.appenglish.entity.Notification;
import com.example.appenglish.entity.User;
import com.example.appenglish.modules.notification.dto.NotificationDto;
import com.example.appenglish.modules.notification.dto.NotificationRequest;
import com.example.appenglish.repository.NotificationRepository;
import com.example.appenglish.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<NotificationDto> getNotificationsByUser(String userId) {
        return notificationRepository.findByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getUnreadNotifications(String userId) {
        return notificationRepository.findByUserIdAndIsRead(userId, false).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificationDto createNotification(NotificationRequest request) {
        User user = null;
        if (request.getUserId() != null) {
            user = userRepository.findById(String.valueOf(request.getUserId()))
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));
        }

        Notification notification = Notification.builder()
                .user(user)
                .message(request.getMessage())
                .isRead(false)
                .metadata(request.getMetadata())
                .build();

        Notification saved = notificationRepository.save(notification);
        return mapToDto(saved);
    }

    @Transactional
    public NotificationDto markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
        notification.setIsRead(true);
        Notification saved = notificationRepository.save(notification);
        return mapToDto(saved);
    }

    @Transactional
    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new RuntimeException("Notification not found with id: " + id);
        }
        notificationRepository.deleteById(id);
    }

    private NotificationDto mapToDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .userId(notification.getUser() != null ? Long.valueOf(notification.getUser().getId()) : null)
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .metadata(notification.getMetadata())
                .build();
    }
}

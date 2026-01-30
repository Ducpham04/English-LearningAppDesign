package com.example.appenglish.service;

import com.example.appenglish.entity.Badge;
import com.example.appenglish.entity.Notification;
import com.example.appenglish.entity.User;
import com.example.appenglish.entity.UserBadge;
import com.example.appenglish.entity.UserBadgeId;
import com.example.appenglish.repository.BadgeRepository;
import com.example.appenglish.repository.NotificationRepository;
import com.example.appenglish.repository.UserBadgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class BadgeService {
    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private UserBadgeRepository userBadgeRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    /**
     * Kiểm tra điều kiện nhận badge và cấp badge.
     */
    public void checkAndGrantBadge(User user, String criteria, Map<String, Object> userData) {
        // Tìm badge dựa trên điều kiện
        Badge badge = badgeRepository.findByCriteria(criteria);

        if (badge == null) {
            throw new RuntimeException("Badge not found!");
        }

        // Kiểm tra điều kiện trong `conditionRule` của badge
        Map<String, Object> condition = badge.getConditionRule();
        if (!checkCondition(condition, userData)) {
            return; // Điều kiện không đạt, không cấp badge.
        }

        // Kiểm tra user đã nhận badge này chưa
        UserBadgeId userBadgeId = new UserBadgeId();
        userBadgeId.setUserId(user.getId());
        userBadgeId.setBadgeId(badge.getId());
        boolean alreadyEarned = userBadgeRepository.existsById(userBadgeId);

        if (alreadyEarned) {
            return; // User đã nhận badge, không cần cấp lại.
        }

        // Cấp badge
        UserBadge userBadge = new UserBadge();
        userBadge.setId(userBadgeId);
        userBadge.setUser(user);
        userBadge.setBadge(badge);
        userBadge.setEarnedAt(LocalDateTime.now());
        userBadgeRepository.save(userBadge);

        // Tạo thông báo
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage("Congratulations! You have earned the badge: " + badge.getName());
        notification.setMetadata(Map.of("badgeId", badge.getId(), "badgeName", badge.getName()));
        notificationRepository.save(notification);
    }

    /**
     * Hàm kiểm tra điều kiện nhận badge.
     *
     * @param condition Điều kiện từ badge (JSON).
     * @param userData Dữ liệu liên quan đến user (JSON).
     * @return true nếu đạt điều kiện, false nếu không.
     */
    private boolean checkCondition(Map<String, Object> condition, Map<String, Object> userData) {
        // Ví dụ: Kiểm tra streak của user
        if (condition.containsKey("streak")) {
            int requiredStreak = (int) condition.get("streak");
            int userStreak = (int) userData.getOrDefault("streak", 0);
            return userStreak >= requiredStreak;
        }

        return false; // Mặc định không đạt nếu không triển khai điều kiện.
    }
}

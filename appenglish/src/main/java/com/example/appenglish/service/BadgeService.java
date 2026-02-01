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
import java.util.Optional;

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
        Optional<Badge> badgeOptional = badgeRepository.findByCriteria(criteria);
        Badge badge = badgeOptional.orElseThrow(() ->
                new IllegalArgumentException("Badge not found for criteria: " + criteria)
        );

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
        // Xử lý trường hợp tiêu chí hoặc dữ liệu user bị null
        if (condition == null || userData == null) {
            System.out.println("Condition or userData is null!");
            return false; // Điều kiện không đạt nếu null
        }

        // Kiểm tra nội dung "streak"
        if (condition.containsKey("streak")) {
            Object streakCondition = condition.get("streak");
            Object userStreakData = userData.get("streak");

            // Xử lý nếu dữ liệu streak bị null
            if (streakCondition == null || userStreakData == null) {
                System.out.println("Condition streak or user streak is missing!");
                return false;
            }

            try {
                int requiredStreak = (int) streakCondition; // Cast an toàn
                int userStreak = (int) userStreakData; // Cast an toàn
                return userStreak >= requiredStreak;
            } catch (ClassCastException e) {
                System.out.println("Failed to cast streak data: " + e.getMessage());
                return false;
            }
        }

        return false; // Điều kiện không đạt mặc định.
    }
}

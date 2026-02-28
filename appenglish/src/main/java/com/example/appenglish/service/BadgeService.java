package com.example.appenglish.service;

import com.example.appenglish.entity.Badge;
import com.example.appenglish.entity.User;
import com.example.appenglish.entity.UserBadge;
import com.example.appenglish.entity.UserBadgeId;
import com.example.appenglish.repository.BadgeRepository;
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
    private NotificationService notificationService;

    /**
     * Kiểm tra điều kiện nhận badge và cấp badge.
     */
    public void checkAndGrantBadge(User user, String criteria, Map<String, Object> userData) {
        // Tìm badge dựa trên điều kiện
        Badge badge = badgeRepository.findByCriteria(criteria)
                .orElseThrow(() -> new IllegalArgumentException("Badge not found for criteria: " + criteria));

        // Kiểm tra điều kiện trong `conditionRule` của badge
        Map<String, Object> condition = badge.getConditionRule();
        if (!checkCondition(condition, userData)) {
            System.out.println("Badge condition not met for user: " + user.getId());
            return; // Điều kiện không đạt, không cấp badge
        }

        // Kiểm tra user đã nhận badge này chưa
        UserBadgeId userBadgeId = new UserBadgeId();
        userBadgeId.setUserId(user.getId());
        userBadgeId.setBadgeId(badge.getId());

        if (userBadgeRepository.existsById(userBadgeId)) {
            System.out.println("User already has this badge: " + badge.getName());
            return; // User đã nhận badge, không cần cấp lại
        }

        // Cấp badge
        UserBadge userBadge = new UserBadge();
        userBadge.setId(userBadgeId);
        userBadge.setUser(user);
        userBadge.setBadge(badge);
        userBadge.setEarnedAt(LocalDateTime.now());
        userBadgeRepository.save(userBadge);

        System.out.println("Badge granted: " + badge.getName() + " to user: " + user.getId());

        // Tạo và gửi thông báo (lưu DB + gửi WebSocket)
        Map<String, Object> metadata = Map.of(
                "type", "BADGE_EARNED",
                "badgeId", badge.getId(),
                "badgeName", badge.getName(),
                "badgeIcon", badge.getIconUrl() != null ? badge.getIconUrl() : ""
        );

        notificationService.sendToUser(
                user,
                "🎉 Congratulations! You have earned the badge: " + badge.getName(),
                metadata
        );
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
            return false;
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
                int requiredStreak = Integer.parseInt(streakCondition.toString());
                int userStreak = Integer.parseInt(userStreakData.toString());
                return userStreak >= requiredStreak;
            } catch (Exception e) {
                System.out.println("Failed to parse streak data: " + e.getMessage());
                return false;
            }
        }

        return false; // Điều kiện không đạt mặc định
    }
}
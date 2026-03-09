package com.example.appenglish.modules.admin.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.entity.User;
import com.example.appenglish.enums.UserRole;
import com.example.appenglish.repository.SessionRepository;
import com.example.appenglish.repository.TopicRepository;
import com.example.appenglish.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
public class AdminStatsController {

    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final SessionRepository sessionRepository;

    @GetMapping("/stats")
    public ResponseEntity<BaseResponse<AdminStats>> getStats(HttpServletRequest request) {
        List<User> allUsers = userRepository.findAll();

        long totalUsers    = allUsers.size();
        long totalStudents = allUsers.stream().filter(u -> u.getRole() == UserRole.STUDENT).count();
        long totalTeachers = allUsers.stream().filter(u -> u.getRole() == UserRole.TEACHER).count();
        long totalAdmins   = allUsers.stream().filter(u -> u.getRole() == UserRole.ADMIN).count();
        long totalTopics   = topicRepository.count();
        long activeTopics  = topicRepository.findByIsActiveTrueOrderByOrderIndexAsc().size();
        long totalSessions = sessionRepository.count();

        List<RecentUser> recentUsers = allUsers.stream()
                .sorted((a, b) -> {
                    if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
                    if (a.getCreatedAt() == null) return 1;
                    if (b.getCreatedAt() == null) return -1;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .limit(5)
                .map(u -> RecentUser.builder()
                        .id(u.getId())
                        .fullName(u.getFullName())
                        .email(u.getEmail())
                        .role(u.getRole() != null ? u.getRole().name().toLowerCase() : "student")
                        .createdAt(u.getCreatedAt() != null ? u.getCreatedAt().toString() : null)
                        .build())
                .collect(Collectors.toList());

        AdminStats stats = AdminStats.builder()
                .totalUsers(totalUsers)
                .totalStudents(totalStudents)
                .totalTeachers(totalTeachers)
                .totalAdmins(totalAdmins)
                .totalTopics(totalTopics)
                .activeTopics(activeTopics)
                .totalSessions(totalSessions)
                .recentUsers(recentUsers)
                .build();

        return ResponseEntity.ok(BaseResponse.ok(stats, "OK", request.getRequestURI()));
    }

    @Data
    @Builder
    public static class AdminStats {
        private long totalUsers;
        private long totalStudents;
        private long totalTeachers;
        private long totalAdmins;
        private long totalTopics;
        private long activeTopics;
        private long totalSessions;
        private List<RecentUser> recentUsers;
    }

    @Data
    @Builder
    public static class RecentUser {
        private String id;
        private String fullName;
        private String email;
        private String role;
        private String createdAt;
    }
}

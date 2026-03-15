package com.example.appenglish.config;

import com.example.appenglish.entity.User;
import com.example.appenglish.enums.UserRole;
import com.example.appenglish.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Chạy tự động sau khi Spring Boot khởi động:
 * - Fix role = NULL cho các user cũ.
 * - Tạo admin mặc định nếu chưa có.
 * Không seed topic/session/lesson/class (sản phẩm đã qua giai đoạn demo).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        fixNullRoles();
        ensureAdminExists();
        // Sản phẩm đã qua giai đoạn demo — không tự seed topic/session/lesson/class nữa.
    }

    // -------------------------------------------------------------------------
    // Fix null roles
    // -------------------------------------------------------------------------
    private void fixNullRoles() {
        List<User> usersWithNullRole = userRepository.findAll().stream()
                .filter(u -> u.getRole() == null)
                .toList();

        if (usersWithNullRole.isEmpty()) return;

        log.info("DataInitializer: Found {} users with null role, fixing...", usersWithNullRole.size());

        for (User user : usersWithNullRole) {
            String email = user.getEmail().toLowerCase();
            if (email.contains("admin")) {
                user.setRole(UserRole.ADMIN);
            } else if (email.contains("teacher")) {
                user.setRole(UserRole.TEACHER);
            } else {
                user.setRole(UserRole.STUDENT);
            }
            userRepository.save(user);
            log.info("DataInitializer: Set role {} for user {}", user.getRole(), user.getEmail());
        }
    }

    // -------------------------------------------------------------------------
    // Ensure admin account exists
    // -------------------------------------------------------------------------
    private void ensureAdminExists() {
        boolean adminExists = userRepository.findAll().stream()
                .anyMatch(u -> u.getRole() == UserRole.ADMIN);

        if (!adminExists) {
            User admin = new User();
            admin.setEmail("admin@lms.com");
            admin.setPasswordHash(passwordEncoder.encode("Admin@123"));
            admin.setFullName("System Admin");
            admin.setRole(UserRole.ADMIN);
            admin.setLevel(1);
            admin.setXp(0);
            admin.setStreak(0);
            admin.setTotalLessonsCompleted(0);
            admin.setFaceIdEnrolled(false);
            userRepository.save(admin);
            log.info("DataInitializer: Created default admin → email: admin@lms.com | password: Admin@123");
        }
    }
}

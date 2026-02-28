package com.example.appenglish.modules.auth.controller;

import com.example.appenglish.entity.User;
import com.example.appenglish.repository.UserRepository;
import com.example.appenglish.service.BadgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/badge")
public class BadgeController {

    @Autowired
    private BadgeService badgeService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/check/{userId}")
    public ResponseEntity<String> checkBadge(
            @PathVariable Long userId,
            @RequestParam String criteria,
            @RequestBody Map<String, Object> userData
    ) {
        // Lấy user thật từ database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Kiểm tra và cấp badge
        badgeService.checkAndGrantBadge(user, criteria, userData);

        return ResponseEntity.ok("Badge checked and granted if conditions met!");
    }
}
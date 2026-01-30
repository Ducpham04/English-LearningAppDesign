package com.example.appenglish.modules.userDetail.service;

import com.example.appenglish.entity.User;
import com.example.appenglish.modules.auth.service.UserConverterService;
import com.example.appenglish.modules.userDetail.dto.UserDataDto;
import com.example.appenglish.modules.userDetail.dto.UserProfileRequest;
import com.example.appenglish.modules.userDetail.dto.UserProfileResponse;
import com.example.appenglish.modules.userDetail.dto.UserXpDto;
import com.example.appenglish.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserConverterService userConverterService;

    public UserDataDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        return userConverterService.convertUserToDataDto(user);
    }

    public UserProfileResponse updateProfile(UserProfileRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        // Update user profile
        if (request.getName() != null) {
            user.setFullName(request.getName());
        }

        if (request.getAvatar() != null) {
            user.setAvatarUrl(request.getAvatar());
        }

        // TODO: Update user settings if needed (save to UserProfile entity)
        if (request.getSettings() != null) {
            // Save settings to UserProfile
            log.info("Settings update requested but not yet implemented");
        }

        userRepository.save(user);

        return UserProfileResponse.builder()
                .id(String.valueOf(user.getId()))
                .name(user.getFullName())
                .email(user.getEmail())
                .avatar(user.getAvatarUrl())
                .settings(request.getSettings())
                .build();
    }

    public List<UserXpDto> getTopUsersByXpInLastWeek() {
        List<Object[]> results = userRepository.findTopUsersByXpInLastWeek();
        return results.stream().map(result -> new UserXpDto(
                (Long) result[0], (String) result[1], (Integer) result[2]
        )).collect(Collectors.toList());
    }
}

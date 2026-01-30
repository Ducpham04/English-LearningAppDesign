package com.example.appenglish.service;


import com.example.appenglish.entity.*;
import com.example.appenglish.modules.auth.dto.RegisterRequest;
import com.example.appenglish.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder  passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserProfileRepository userProfileRepository;
    private final XpRepository xpRepository;
    private final FaceIdRepository faceIdRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        if (user.getRole() != null && user.getRole().getName() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));
        }
        if (user.getRole() != null && user.getRole().getPermissions() != null) {
            user.getRole().getPermissions().forEach(p -> authorities.add(new SimpleGrantedAuthority(p.getCode())));
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash() == null ? "" : user.getPasswordHash())
                .authorities(authorities)
                .build();
    }

    // Sửa kiểu trả về từ void -> boolean
    @Transactional
    public boolean register(RegisterRequest req) {

        if (userRepository.existsByEmail(req.getEmail())) {
            return false;
        }

        if (req.getRole() == null) {
            return false;
        }

        // 1. Map role name
        String roleName;
        if ("STUDENT".equalsIgnoreCase(req.getRole())) {
            roleName = "STUDENT";
        } else if ("TEACHER".equalsIgnoreCase(req.getRole())) {
            roleName = "TEACHER";
        } else {
            return false;
        }

        // 2. LẤY ROLE TỪ DB (KHÔNG new)
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() ->
                        new RuntimeException("Role not found in DB: " + roleName)
                );
        // 3. Tạo user
        User newUser = new User();
        newUser.setEmail(req.getEmail());
        newUser.setFullName(req.getFullName());
        newUser.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        newUser.setRole(role);
        newUser.setProvider(User.AuthProvider.LOCAL);

        userRepository.save(newUser);

        UserProfile profile = new UserProfile();
        profile.setUser(newUser);

        // Set level with default value if not provided
        String levelSelection = req.getLevelSelection();
        if (levelSelection == null || levelSelection.trim().isEmpty()) {
            levelSelection = "Beginner"; // Default level
        }
        profile.setLevel(UserProfile.Level.valueOf(levelSelection));
        profile.setLearningGoal(req.getLearningGoal());
        profile.setCurrentStreak(0);
        userProfileRepository.save(profile);

        XPLog xpLog = new XPLog();
        xpLog.setUser(newUser);

        xpRepository.save(xpLog);

        FaceVerificationLog faceVerificationLog = new FaceVerificationLog();
        faceVerificationLog.setUser(newUser);

        faceIdRepository .save(faceVerificationLog);

        return true;
    }


    public List<User> getUsers() {
        return userRepository.findAll();
    }


}


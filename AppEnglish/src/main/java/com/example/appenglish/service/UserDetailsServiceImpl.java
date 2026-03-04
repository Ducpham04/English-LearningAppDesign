package com.example.appenglish.service;

import com.example.appenglish.entity.User;
import com.example.appenglish.entity.UserProfile;
import com.example.appenglish.entity.XPLog;
import com.example.appenglish.entity.FaceVerificationLog;
import com.example.appenglish.entity.Role;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserProfileRepository userProfileRepository;
    private final XpRepository xpRepository;
    private final FaceIdRepository faceIdRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        if (user.getRole() != null) {
            // Role entity có trường name (String), dùng trực tiếp
            authorities.add(new SimpleGrantedAuthority(user.getRole().getName()));
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

        // Map role string to Role entity (dùng role_id)
        Role role;
        if ("STUDENT".equalsIgnoreCase(req.getRole())) {
            role = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Role not found: ROLE_USER"));
        } else if ("TEACHER".equalsIgnoreCase(req.getRole())) {
            role = roleRepository.findByName("ROLE_TEACHER")
                    .orElseThrow(() -> new RuntimeException("Role not found: ROLE_TEACHER"));
        } else if ("ADMIN".equalsIgnoreCase(req.getRole())) {
            role = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("Role not found: ROLE_ADMIN"));
        } else {
            return false;
        }

        // Create user với Role entity (dùng role_id)
        User newUser = User.builder()
                .email(req.getEmail())
                .fullName(req.getFullName())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role(role)
                .level(1)
                .xp(0)
                .streak(0)
                .totalLessonsCompleted(0)
                .faceIdEnrolled(false)
                .build();

        userRepository.save(newUser);

        // Create user profile
        UserProfile profile = new UserProfile();
        profile.setUser(newUser);
        profile.setLevel(UserProfile.Level.Beginner);
        profile.setCurrentStreak(0);
        userProfileRepository.save(profile);

        // Create XP log
        XPLog xpLog = new XPLog();
        xpLog.setUser(newUser);
        xpRepository.save(xpLog);

        // Create face verification log
        FaceVerificationLog faceVerificationLog = new FaceVerificationLog();
        faceVerificationLog.setUser(newUser);
        faceIdRepository.save(faceVerificationLog);

        return true;
    }


    public List<User> getUsers() {
        return userRepository.findAll();
    }


}

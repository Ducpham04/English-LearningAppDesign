package com.example.appenglish.service;


import com.example.appenglish.entity.User;
import com.example.appenglish.enums.UserRole;
import com.example.appenglish.common.exception.ApiException;
import com.example.appenglish.modules.auth.dto.RegisterRequest;
import com.example.appenglish.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder  passwordEncoder;
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        UserRole effectiveRole = user.getRole() != null ? user.getRole() : UserRole.STUDENT;
        authorities.add(new SimpleGrantedAuthority("ROLE_" + effectiveRole.name()));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash() == null ? "" : user.getPasswordHash())
                .authorities(authorities)
                .build();
    }

    // Sửa kiểu trả về từ void -> boolean
    @Transactional
    public boolean register(RegisterRequest req) {

        log.info("Start register user: email={}, role={}", req.getEmail(), req.getRole());

        if (userRepository.existsByEmail(req.getEmail())) {
            log.warn("Register failed: email already exists {}", req.getEmail());
            throw new ApiException("Email đã tồn tại, vui lòng dùng email khác!", "DUPLICATE_EMAIL");
        }

        // 1. Map role name -> enum
        UserRole userRole;
        String role = req.getRole() != null ? req.getRole().trim() : "";
        if ("STUDENT".equalsIgnoreCase(role)) {
            userRole = UserRole.STUDENT;
        } else if ("TEACHER".equalsIgnoreCase(role)) {
            userRole = UserRole.TEACHER;
        } else if ("ADMIN".equalsIgnoreCase(role)) {
            userRole = UserRole.ADMIN;
        } else {
            log.warn("Register failed: unsupported role {} for email={}", req.getRole(), req.getEmail());
            throw new ApiException("Vai trò không hợp lệ", "INVALID_ROLE");
        }

        // 2. Tạo user (id để Hibernate tự generate)
        User newUser = new User();
        newUser.setEmail(req.getEmail());
        newUser.setFullName(req.getFullName());
        newUser.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        newUser.setRole(userRole);

        userRepository.save(newUser);
        log.info("Register success: new user id={}, email={}", newUser.getId(), newUser.getEmail());
        return true;
    }


    public List<User> getUsers() {
        return userRepository.findAll();
    }


}


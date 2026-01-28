package com.example.appenglish.service;


import com.example.appenglish.entity.Role;
import com.example.appenglish.entity.User;
import com.example.appenglish.modules.auth.dto.RegisterRequest;
import com.example.appenglish.repository.RoleRepository;
import com.example.appenglish.repository.UserRepository;
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
    public boolean register(RegisterRequest req) {
        // 1. Nếu trùng email -> Trả về false luôn (Không ném lỗi)
        if (userRepository.existsByEmail(req.getEmail())) {
            return false;
        }
        Role role = roleRepository.findByName("ROLE_USER") ;
        if (role == null) {
            return false;
        }else
        {
            System.out.println("Đã có id " + role.getName());
        }

        // 2. Logic lưu user như cũ
        User newUser = new User();
        newUser.setEmail(req.getEmail());
        newUser.setFullName(req.getFullName());
        newUser.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        newUser.setRole(roleRepository.findByName("ROLE_USER"));
        newUser.setProvider(User.AuthProvider.LOCAL);

        userRepository.save(newUser);

        // 3. Trả về true báo thành công
        return true;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }


}


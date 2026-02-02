package com.example.appenglish.security;

import com.example.appenglish.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPrincipal implements UserDetails {

    private Long id; // Trường quan trọng để SecurityUtils trích xuất ID

    private String email;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    /**
     * Chuyển đổi từ Entity User sang UserPrincipal.
     * Ánh xạ passwordHash từ DB vào password của Security.
     */
    public static UserPrincipal create(User user) {
        // 1. Lấy quyền hạn từ Role của User
        // Dựa trên init.sql, Role có tên như ROLE_USER, ROLE_ADMIN
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole() != null ? user.getRole().getName() : "ROLE_USER")
        );

        return UserPrincipal.builder()
                .id(user.getId())
                .email(user.getEmail())
                // Lưu ý: Sử dụng getPasswordHash() vì trong Entity bạn đặt tên là passwordHash
                .password(user.getPasswordHash()) 
                .authorities(authorities)
                .build();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
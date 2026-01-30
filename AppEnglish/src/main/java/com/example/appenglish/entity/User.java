package com.example.appenglish.entity;

import com.example.appenglish.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;


import java.time.LocalDateTime;
/**
 * Entity Người dùng trung tâm.
 * ĐIỂM KỸ THUẬT:
 * - Soft Delete: Dùng @SQLDelete để không xóa mất dữ liệu thật.
 * - Social Login: Password có thể null nếu login bằng Google.
 */
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email"), // Tìm nhanh khi Login
        @Index(name = "idx_user_provider", columnList = "provider_id") // Tìm nhanh user MXH
})
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL") // Mặc định lọc bỏ user đã xóa
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash; // Nullable (cho Social Login)

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    // --- PHÂN QUYỀN & TRẠNG THÁI ---
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider; // LOCAL, GOOGLE, FACEBOOK

    public enum AuthProvider { LOCAL, GOOGLE, FACEBOOK }

    @Column(name = "provider_id")
    private String providerId; // ID trả về từ Google/FB

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // Cờ đánh dấu đã xóa (Soft Delete)
}



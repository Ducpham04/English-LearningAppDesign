package com.example.appenglish.entity;

import com.example.appenglish.enums.AuthProvider;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filter;

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
@FilterDef(name = "deletedFilter", defaultCondition = "deleted_at IS NULL")
@Filter(name = "deletedFilter")
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
    private AuthProvider provider; // Sử dụng Enum từ package com.example.appenglish.enums

    @Column(name = "provider_id")
    private String providerId; // ID trả về từ Google/FB

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // Cờ đánh dấu đã xóa (Soft Delete)
}
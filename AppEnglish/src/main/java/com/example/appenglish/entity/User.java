package com.example.appenglish.entity;

import com.example.appenglish.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    // Dùng role_id (foreign key) thay vì @Enumerated String
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @Builder.Default
    private Integer level = 1;
    
    @Builder.Default
    private Integer xp = 0;
    
    @Builder.Default
    private Integer streak = 0;

    @Column(name = "total_lessons_completed")
    @Builder.Default
    private Integer totalLessonsCompleted = 0;

    @Column(name = "face_id_enrolled")
    @Builder.Default
    private Boolean faceIdEnrolled = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
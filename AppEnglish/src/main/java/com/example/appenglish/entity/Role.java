package com.example.AppEnglish.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Role extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name; // VD: "TEACHER", "ADMIN"

    @Column(columnDefinition = "TEXT")
    private String description;

    // --- QUAN HỆ MANY-TO-MANY VỚI PERMISSION ---
    // JPA sẽ tự tạo bảng trung gian "role_permissions" cho bạn
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();


}

package com.example.appenglish.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permissions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Permission extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name; // VD: "Create Course"

    @Column(nullable = false, unique = true)
    private String code; // VD: "COURSE_CREATE" (Dùng để check trong code)

    private String module; // VD: "ACADEMY", "SYSTEM"

    @Column(columnDefinition = "TEXT")
    private String description;
}
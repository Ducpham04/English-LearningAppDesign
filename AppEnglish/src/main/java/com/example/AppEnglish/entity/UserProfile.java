package com.example.appenglish.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Thông tin mở rộng của User.
 * Tách ra bảng riêng để bảng Users nhẹ hơn, giúp Login nhanh hơn.
 */
@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDate dob;
    private String phone;
    private String country;

    @Column(name = "learning_goal", columnDefinition = "TEXT")
    private String learningGoal; // Mục tiêu học tập
}
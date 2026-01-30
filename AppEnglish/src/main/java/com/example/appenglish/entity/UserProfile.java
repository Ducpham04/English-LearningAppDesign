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

    @Enumerated(EnumType.STRING)
    @Column(name ="level")
    private Level  level;

    @Column(name = "current_streak", columnDefinition = "integer default 0")
    private Integer currentStreak = 0;

    @Column(name = "last_activity_date")
    private LocalDate lastActivityDate;
    public enum Level {Beginner,Elementary, Intermediate, Upper_Intermediate , Advanced ,Proficient}

}
package com.example.appenglish.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "user_badges")
@Getter
@Setter
public class UserBadge {
    @EmbeddedId
    private UserBadgeId id;// Khóa chính tổng hợp

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")// Map với trường userId của UserBadgeId
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("badgeId") // Map với trường badgeId của UserBadgeId
    private Badge badge;

    private LocalDateTime earnedAt;// Thời gian nhận badge
}


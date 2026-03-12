package com.example.appenglish.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_badges")
@Getter
@Setter
public class UserBadge {
    @EmbeddedId
    private UserBadgeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("badgeId")
    private Badge badge;

    private LocalDateTime earnedAt;
}

@Embeddable
@Getter @Setter @EqualsAndHashCode
class UserBadgeId implements Serializable {
    private String userId;
    private Long badgeId;
}
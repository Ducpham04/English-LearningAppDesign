package com.example.AppEnglish.entity;

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
    private Long userId;
    private Long badgeId;
}
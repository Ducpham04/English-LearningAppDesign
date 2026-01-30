package com.example.appenglish.entity;

import jakarta.persistence.*;
import lombok.*;
// BỔ SUNG CÁC IMPORT QUAN TRỌNG NÀY
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

// Class UserBadgeId nằm cùng file
@Embeddable
@Getter @Setter @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor // Nên thêm constructor cho Embeddable ID
class UserBadgeId implements Serializable {
    private Long userId;
    private Long badgeId;
}
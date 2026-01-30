package com.example.appenglish.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter @Setter @EqualsAndHashCode
public class UserBadgeId implements Serializable {
    private Long userId;
    private Long badgeId;
}

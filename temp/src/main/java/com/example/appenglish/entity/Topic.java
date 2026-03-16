package com.example.appenglish.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "topics")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Topic extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "difficulty_level")
    private String difficultyLevel; // BEGINNER, INTERMEDIATE, ADVANCED

    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "estimated_duration_minutes")
    private Integer estimatedDurationMinutes;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "is_active")
    private Boolean isActive = true;
}

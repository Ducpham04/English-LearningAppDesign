package com.example.AppEnglish.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "sessions")
@Getter
@Setter
public class Session extends BaseEntity {

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "order_index")
    private Integer orderIndex; // Thứ tự chương (Chương 1, Chương 2)

    private String level; // beginner, intermediate, advanced

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "estimated_time")
    private Integer estimatedTime; // In minutes

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Curriculum course;

    // Một chương có nhiều bài học
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    private List<Lesson> lessons;
}
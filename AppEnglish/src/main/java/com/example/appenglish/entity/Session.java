package com.example.appenglish.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OrderBy;

import java.util.ArrayList;
import java.util.List;

/**
 * Session (chủ đề/buổi học) nằm trong một khóa học (Curriculum)
 * và chứa nhiều Lesson.
 */
@Entity
@Table(name = "sessions")
@Getter
@Setter
public class Session extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Curriculum course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Column(name = "order_index")
    private Integer orderIndex;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @jakarta.persistence.OrderBy("orderIndex ASC")
    private List<Lesson> lessons = new ArrayList<>();
}


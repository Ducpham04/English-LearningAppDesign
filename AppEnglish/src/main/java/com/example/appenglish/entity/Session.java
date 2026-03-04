package com.example.appenglish.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "sessions")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Session extends BaseEntity {

    private String title;

    @Column(name = "order_index")
    private Integer orderIndex; // Thứ tự chương (Chương 1, Chương 2)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Curriculum course;

    // Một chương có nhiều bài học
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    private List<Lesson> lessons;
}
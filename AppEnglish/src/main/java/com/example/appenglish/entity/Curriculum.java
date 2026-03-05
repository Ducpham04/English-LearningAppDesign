package com.example.AppEnglish.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
@Getter
@Setter
public class Curriculum extends BaseEntity {

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    private Integer price; // 0 = Free

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private User teacher; // Người tạo khóa học

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    @OrderBy("orderIndex ASC")
    private List<Session> sessions = new ArrayList<>();
}

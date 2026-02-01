package com.example.appenglish.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "sessions")
@Getter @Setter 
@NoArgsConstructor @AllArgsConstructor 
@Builder
public class Session extends BaseEntity {
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description; // THÊM DÒNG NÀY

    private String thumbnail;   // THÊM DÒNG NÀY

    @Column(name = "order_index")
    private Integer orderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Curriculum course;

    @OneToMany(mappedBy = "session")
    private List<Lesson> lessons;
}
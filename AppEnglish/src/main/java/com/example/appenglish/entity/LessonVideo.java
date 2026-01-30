package com.example.appenglish.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lesson_videos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LessonVideo extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @Column(name = "video_url", nullable = false)
    private String videoUrl;

    @Column(name = "subtitles_url") // Đảm bảo trường này tồn tại
    private String subtitlesUrl;

    private Integer duration; // Thời lượng video (giây)
}
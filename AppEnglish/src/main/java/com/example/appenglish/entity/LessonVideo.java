package com.example.appenglish.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;

/**
 * Chi tiết Video bài học.
 * TỐI ƯU: Lưu transcript vào JSONB thay vì bảng riêng để query nhanh.
 */
@Entity
@Table(name = "lesson_videos")
@Getter @Setter
public class LessonVideo {

    @Id
    private Long lessonId; // PK = FK (1-1 với Lesson)

    @OneToOne
    @MapsId
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @Column(name = "video_url")
    private String videoUrl; // Link S3/MP4 audio từ YouTube download

    @Column(name = "youtube_url")
    private String youtubeUrl; // URL YouTube gốc để embed player

    // --- JSONB TRANSCRIPT ---
    // Cấu trúc: [{"start": 0, "end": 2, "text": "Hello"}, ...]
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "transcript_json", columnDefinition = "jsonb")
    private List<Map<String, Object>> transcriptJson;
}
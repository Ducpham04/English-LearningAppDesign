package com.example.appenglish.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.List;
import java.util.Map;

@Entity
@Table(name = "lesson_videos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LessonVideo extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @Column(name = "video_url", nullable = false)
    private String videoUrl;

    @Column(name = "subtitles_url")
    private String subtitlesUrl;

    private Integer duration; // Thời lượng video (giây)

    // SỬA: Thêm trường này để Service có thể gọi getTranscriptJson/setTranscriptJson
    // Sử dụng JsonType của hypersistence-utils để map với JSONB của PostgreSQL
    @Type(JsonType.class)
    @Column(name = "transcript_json", columnDefinition = "jsonb")
    private List<Map<String, Object>> transcriptJson;
}
package com.example.appenglish.entity;

import com.example.appenglish.enums.PronunciationAttemptStatus;
import com.example.appenglish.enums.S3UploadStatus;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.util.Map;

@Entity
@Table(name = "pronunciation_attempts", indexes = {
        @Index(name = "idx_pron_attempt_user", columnList = "user_id"),
        @Index(name = "idx_pron_attempt_lesson", columnList = "lesson_id")
})
@Getter
@Setter
public class PronunciationAttempt extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Column(name = "expected_text", columnDefinition = "TEXT")
    private String expectedText;

    @Column(name = "recognized_text", columnDefinition = "TEXT")
    private String recognizedText;

    @Column(name = "pronunciation_score")
    private Double pronunciationScore;

    @Column(name = "accuracy_score")
    private Double accuracyScore;

    @Column(name = "fluency_score")
    private Double fluencyScore;

    @Column(name = "prosody_score")
    private Double prosodyScore;

    @Column(name = "completeness_score")
    private Double completenessScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PronunciationAttemptStatus status = PronunciationAttemptStatus.PROCESSING;

    @Enumerated(EnumType.STRING)
    @Column(name = "s3_status", nullable = false)
    private S3UploadStatus s3Status = S3UploadStatus.PENDING;

    @Column(name = "s3_key")
    private String s3Key;

    @Column(name = "audio_content_type")
    private String audioContentType;

    @Type(JsonType.class)
    @Column(name = "feedback_json", columnDefinition = "jsonb")
    private Map<String, Object> feedback;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
}

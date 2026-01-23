package com.example.appenglish.entity;

import com.example.appenglish.entity.BaseEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import java.util.Map;

/**
 * Lưu lịch sử chat với AI.
 * Dùng JSONB để lưu metadata (số token, độ cảm xúc) phục vụ Analytics.
 */
@Entity
@Table(name = "ai_messages")
@Getter @Setter
public class AIMessage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private SenderRole sender; // USER, AI

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata; // { "tokens": 150, "model": "gpt-4" }
}
enum SenderRole { USER, AI }
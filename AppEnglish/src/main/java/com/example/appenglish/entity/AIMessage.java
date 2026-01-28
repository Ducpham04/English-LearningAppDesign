package com.example.appenglish.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.Map;

/**
 * Lưu lịch sử chat với AI.
 * Dùng JSONB để lưu metadata (số token, độ cảm xúc) phục vụ Analytics.
 * Liên kết với AIConversation để truy vấn "toàn bộ tin nhắn của một cuộc hội thoại".
 */
@Entity
@Table(name = "ai_messages", indexes = {
        @Index(name = "idx_ai_msg_conversation", columnList = "conversation_id"),
        @Index(name = "idx_ai_msg_user", columnList = "user_id")
})
@Getter @Setter
public class AiMessage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private AIConversation conversation;

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


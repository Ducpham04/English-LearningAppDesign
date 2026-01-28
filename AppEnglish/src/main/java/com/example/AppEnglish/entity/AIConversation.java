package com.example.appenglish.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ai_conversations", indexes = {
        @Index(name = "idx_ai_conv_user", columnList = "user_id"),
        @Index(name = "idx_ai_conv_started", columnList = "started_at")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AIConversation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String title; // Tự động tóm tắt chủ đề (VD: "Luyện tập thì quá khứ")

    // Context ban đầu cho AI (System Prompt)
    // VD: "Bạn là giáo viên IELTS khó tính, hãy sửa lỗi ngữ pháp cho user."
    @Column(columnDefinition = "TEXT")
    private String systemContext;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    @Builder.Default
    private List<AIMessage> messages = new ArrayList<>();
}
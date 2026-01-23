package com.example.appenglish.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_conversations", indexes = {
        @Index(name = "idx_ai_conv_user", columnList = "user_id") // Tìm lịch sử chat của user nhanh
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
}
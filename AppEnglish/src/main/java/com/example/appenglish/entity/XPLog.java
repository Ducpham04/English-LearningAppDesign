package com.example.AppEnglish.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "xp_logs")
@Getter
@Setter
public class XPLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    // QUAN TRỌNG: Định nghĩa rõ tên cột để tránh trùng từ khóa "user" của Postgres
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Integer amount;

    // Nếu "action" gây lỗi (hiếm gặp nhưng có thể), hãy đổi tên cột trong DB
    @Column(name = "action_type")
    private String action;
}
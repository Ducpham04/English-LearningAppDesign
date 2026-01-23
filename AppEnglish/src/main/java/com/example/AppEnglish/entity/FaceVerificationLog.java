package com.example.appenglish.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Nhật ký xác thực khuôn mặt.
 * Có cơ chế tự hủy (expired_at) để tuân thủ chính sách bảo mật dữ liệu sinh trắc học.
 */
@Entity
@Table(name = "face_verification_logs")
@Getter @Setter
public class FaceVerificationLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Float confidence; // Độ tin cậy AI (0.0 - 1.0)
    private Boolean isVerified; // Kết quả

    @Column(name = "expired_at")
    private LocalDateTime expiredAt; // Thời điểm xóa ảnh/log
}

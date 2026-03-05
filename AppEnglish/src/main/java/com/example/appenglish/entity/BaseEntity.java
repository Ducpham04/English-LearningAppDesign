package com.example.AppEnglish.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
/**
 * Lớp cha (MappedSuperclass) cho toàn bộ hệ thống.
 * MỤC ĐÍCH:
 * 1. Chuẩn hóa ID (Khóa chính).
 * 2. Tự động Audit (Ghi lại thời gian tạo và sửa đổi) giúp truy vết dữ liệu.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
public abstract class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Dùng BigInt trong DB để chứa lượng dữ liệu lớn

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // Thời điểm tạo bản ghi

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // Thời điểm cập nhật cuối cùng


}
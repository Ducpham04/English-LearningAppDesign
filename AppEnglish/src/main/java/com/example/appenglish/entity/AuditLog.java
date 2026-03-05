package com.example.AppEnglish.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.Map;

@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_actor", columnList = "actor_id"),      // Tìm theo người thực hiện
        @Index(name = "idx_audit_table_record", columnList = "table_name, record_id") // Tìm lịch sử của 1 bản ghi cụ thể
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLog extends BaseEntity {

    /**
     * ID của người thực hiện hành động (Admin hoặc User).
     * Lưu ý: Không dùng @ManyToOne User ở đây để tránh lỗi
     * nếu User đó bị xóa vĩnh viễn khỏi hệ thống sau này.
     */
    @Column(name = "actor_id")
    private Long actorId;

    /**
     * Tên hành động.
     * Ví dụ: "CREATE", "UPDATE", "DELETE", "LOGIN_FAILED", "BAN_USER"
     */
    @Column(nullable = false)
    private String action;

    /**
     * Tên bảng bị tác động.
     * Ví dụ: "users", "test_results", "lessons"
     */
    @Column(name = "table_name")
    private String tableName;

    /**
     * ID của bản ghi bị tác động.
     */
    @Column(name = "record_id")
    private Long recordId;

    /**
     * --- JSONB OLD VALUE ---
     * Lưu lại giá trị cũ trước khi thay đổi để đối chiếu (hoặc rollback).
     * Ví dụ: { "score": 5, "status": "failed" }
     * Chỉ cần lưu những trường bị thay đổi hoặc snapshot toàn bộ entity cũ.
     */
    @Type(JsonType.class)
    @Column(name = "old_value", columnDefinition = "jsonb")
    private Map<String, Object> oldValue;

    /**
     * (Tùy chọn) Giá trị mới sau khi sửa.
     */
    @Type(JsonType.class)
    @Column(name = "new_value", columnDefinition = "jsonb")
    private Map<String, Object> newValue;

    /**
     * IP hoặc User Agent của người thực hiện (để tăng tính bảo mật).
     */
    @Column(name = "ip_address")
    private String ipAddress;
}
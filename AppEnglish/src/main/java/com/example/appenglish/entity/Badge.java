package com.example.appenglish.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "badges")
@Getter @Setter
public class Badge extends BaseEntity {
    private String name;// Tên huy hiệu
    private String iconUrl;// URL của icon huy hiệu

    // Thêm trường criteria để ánh xạ phương thức trong repository
    private String criteria; // Tiêu chí phân loại huy hiệu (VD: "STREAK_7_DAYS")

    // JSONB: Điều kiện nhận huy hiệu { "streak": 7 }
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> conditionRule;// Rule điều kiện nhận badge
}

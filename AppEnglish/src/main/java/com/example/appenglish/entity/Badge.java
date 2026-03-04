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
    private String name;

    private String iconUrl;

    // JSONB: Điều kiện nhận huy hiệu { "streak": 7 }
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> conditionRule;
}

package com.example.appenglish.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.util.Map;

@Entity
@Table(name = "notifications")
@Getter @Setter
public class Notification extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String message;
    private Boolean isRead = false;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;
}

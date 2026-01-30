package com.example.appenglish.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter @Setter
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID sẽ tăng dần: 1, 2, 3...
    private Long id;

    @CreationTimestamp
    private LocalDateTime createdAt;

<<<<<<< HEAD:AppEnglish/src/main/java/com/example/AppEnglish/entity/BaseEntity.java
    @UpdateTimestamp
    private LocalDateTime updatedAt;
=======
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // Thời điểm cập nhật cuối cùng


>>>>>>> origin/develop:AppEnglish/src/main/java/com/example/appenglish/entity/BaseEntity.java
}
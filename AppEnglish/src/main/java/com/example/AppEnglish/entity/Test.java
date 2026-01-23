package com.example.appenglish.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tests")
@Getter @Setter
public class Test extends BaseEntity {
    private String title;
    private Integer duration; // Phút (null = không giới hạn)
    private Integer passScore; // Điểm sàn

    @Column(name = "is_face_required")
    private Boolean isFaceRequired; // Yêu cầu FaceID không?
}
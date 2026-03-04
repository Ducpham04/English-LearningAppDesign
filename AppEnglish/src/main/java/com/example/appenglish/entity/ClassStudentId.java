package com.example.appenglish.entity;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Getter @Setter @EqualsAndHashCode
public class ClassStudentId implements Serializable {
    private Long classId;
    private String studentId;  // Changed to String to match User.id (UUID)
}

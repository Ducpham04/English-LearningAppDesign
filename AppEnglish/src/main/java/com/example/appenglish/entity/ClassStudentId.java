package com.example.appenglish.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

/**
 * Composite key cho ClassStudent.
 */
@Embeddable
@Getter @Setter @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
public class ClassStudentId implements Serializable {
    private Long classId;
    private Long studentId;
}

package com.example.appenglish.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class ClassStudentId implements Serializable {
    private Long classId;
    private String studentId; // matches User.id (UUID)
}

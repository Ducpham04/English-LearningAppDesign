package com.example.appenglish.entity;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVocabId implements Serializable {
    private Long userId;
    private Long vocabularyId; // Phải chính xác là vocabularyId
}
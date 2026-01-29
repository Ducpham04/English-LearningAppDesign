package com.example.appenglish.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vocabularies")
@Getter @Setter 
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class Vocabulary extends BaseEntity {
    // ID tăng dần 1, 2, 3... kế thừa từ BaseEntity (strategy = GenerationType.IDENTITY)

    @Column(nullable = false)
    private String word;

    private String phonetic;

    @Column(columnDefinition = "TEXT")
    private String definition;

    @Column(columnDefinition = "TEXT")
    private String example;

    private String level; 
}
package com.example.appenglish.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "vocabularies")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Vocabulary extends BaseEntity {

    @Column(nullable = false)
    private String word;

    private String level; // EASY, MEDIUM, HARD
    private Long userId;  // ID người học/người tạo

    private String phonetic;
    private String audioUrl;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<Map<String, String>> meanings;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<String> tags;
}
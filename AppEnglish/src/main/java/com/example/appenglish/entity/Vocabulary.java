package com.example.appenglish.entity;

<<<<<<< HEAD:AppEnglish/src/main/java/com/example/AppEnglish/entity/Vocabulary.java
import jakarta.persistence.*;
import lombok.*;

=======
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Kho từ vựng Global.
 * Dùng JSONB cho 'meanings' để lưu nhiều nghĩa + ví dụ linh hoạt.
 */
>>>>>>> origin/develop:AppEnglish/src/main/java/com/example/appenglish/entity/Vocabulary.java
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

<<<<<<< HEAD:AppEnglish/src/main/java/com/example/AppEnglish/entity/Vocabulary.java
    @Column(columnDefinition = "TEXT")
    private String example;

    private String level; 
}
=======
    // Cấu trúc: ["IELTS", "Food", "A1"]
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<String> tags;

    @OneToMany(mappedBy = "vocabulary")
    private List<LessonVocabulary> lessonVocabularies = new ArrayList<>();
}
>>>>>>> origin/develop:AppEnglish/src/main/java/com/example/appenglish/entity/Vocabulary.java

package com.example.appenglish.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Composite Key cho UserVocabularyProgress.
 * Gồm User ID + Vocabulary ID.
 */
@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserVocabId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "vocab_id")
    private Long vocabId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserVocabId that = (UserVocabId) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(vocabId, that.vocabId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, vocabId);
    }
}

package com.example.appenglish.repository;

import com.example.appenglish.entity.Vocabulary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {

    Optional<Vocabulary> findByWordIgnoreCase(String word);

    List<Vocabulary> findByWordContainingIgnoreCase(String word);
}

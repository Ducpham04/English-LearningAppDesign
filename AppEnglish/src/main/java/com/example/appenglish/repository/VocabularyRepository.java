package com.example.appenglish.repository;

import com.example.appenglish.entity.Vocabulary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {
    
   @Query("SELECT v FROM Vocabulary v WHERE " +
       "(:word IS NULL OR LOWER(v.word) LIKE LOWER(CONCAT('%', :word, '%'))) AND " +
       "(:level IS NULL OR v.level = :level)")
Page<Vocabulary> findWithFilters(String word, String level, Pageable pageable);
}
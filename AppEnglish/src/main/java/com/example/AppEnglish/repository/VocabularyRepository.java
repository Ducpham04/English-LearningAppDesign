package com.example.AppEnglish.repository;

import com.example.AppEnglish.entity.Vocabulary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VocabularyRepository extends JpaRepository<Vocabulary, Long>,
        JpaSpecificationExecutor<Vocabulary> {

    @Query(value = """
                SELECT v FROM Vocabulary v
                LEFT JOIN FETCH v.session
                WHERE (:keyword IS NULL OR LOWER(v.word) LIKE LOWER(CONCAT('%', CAST(:keyword AS text), '%')))
                AND (:sessionId IS NULL OR v.session.id = :sessionId)
            """, countQuery = """
                SELECT COUNT(v) FROM Vocabulary v
                WHERE (:keyword IS NULL OR LOWER(v.word) LIKE LOWER(CONCAT('%', CAST(:keyword AS text), '%')))
                AND (:sessionId IS NULL OR v.session.id = :sessionId)
            """)
    Page<Vocabulary> searchByKeywordAndSession(@Param("keyword") String keyword,
            @Param("sessionId") Long sessionId,
            Pageable pageable);
}

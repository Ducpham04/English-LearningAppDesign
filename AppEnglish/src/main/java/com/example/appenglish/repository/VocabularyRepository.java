package com.example.appenglish.repository;

import com.example.appenglish.entity.Vocabulary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho Vocabulary (Từ vựng).
 */
@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {

    /**
     * Tìm từ vựng theo từ.
     */
    Optional<Vocabulary> findByWordIgnoreCase(String word);

    /**
     * Tìm các từ vựng chứa từ khóa (tìm kiếm) với phân trang.
     */
    Page<Vocabulary> findByWordContainingIgnoreCase(String word, Pageable pageable);

    /**
     * Tìm các từ vựng theo thẻ (tag).
     */
    @Query(nativeQuery = true, value = "SELECT v.* FROM vocabularies v WHERE v.tags LIKE CONCAT('%', :tag, '%')")
    List<Vocabulary> findByTag(@Param("tag") String tag);

    /**
     * Kiểm tra từ vựng có tồn tại.
     */
    boolean existsByWordIgnoreCase(String word);

    /**
     * Đếm số từ vựng.
     */
    long count();

    /**
     * Tìm các từ vựng mới nhất.
     */
    @Query("SELECT v FROM Vocabulary v ORDER BY v.createdAt DESC")
    List<Vocabulary> findLatestVocabularies(Pageable pageable);

    /**
     * Tìm các từ vựng bằng phương pháp full-text search.
     */
    @Query("SELECT v FROM Vocabulary v WHERE LOWER(v.word) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Vocabulary> searchByWord(@Param("searchTerm") String searchTerm);

    /**
     * Tìm tất cả từ vựng được sắp xếp theo từ.
     */
    List<Vocabulary> findAllByOrderByWordAsc();

    /**
     * Tìm các từ vựng trong một phạm vi giới hạn (phân trang).
     * Sử dụng Spring Data Pageable để hỗ trợ phân trang tự động.
     */
    Page<Vocabulary> findAll(Pageable pageable);
}

package com.example.appenglish.modules.vocabulary.service;

import com.example.appenglish.entity.UserVocabularyProgress;
import com.example.appenglish.entity.Vocabulary;
import com.example.appenglish.modules.vocabulary.dto.VocabularyRequest;
import com.example.appenglish.modules.vocabulary.dto.VocabularyResponse;
import com.example.appenglish.repository.UserVocabularyProgressRepository;
import com.example.appenglish.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý logic cho Vocabulary và Spaced Repetition (SRS).
 */
@Service
@RequiredArgsConstructor
public class VocabularyService {

    private final VocabularyRepository repository;
    private final UserVocabularyProgressRepository userVocabProgressRepository;

    /**
     * Lấy danh sách từ vựng tích hợp Search (word), Filter (level, userId) và Phân trang.
     *
     */
    public Page<VocabularyResponse> getAll(String word, String level, Long userId, Pageable pageable) {
        return repository.findWithFilters(word, level, userId, pageable)
                .map(this::toResponse);
    }

    /**
     * Tạo mới từ vựng với các thông tin chính (word, level, userId).
     *
     */
    @Transactional
    public VocabularyResponse create(VocabularyRequest req) {
        Vocabulary v = Vocabulary.builder()
                .word(req.getWord())
                .level(req.getLevel())
                .userId(req.getUserId())
                .build();
        
        return toResponse(repository.save(v));
    }

    /**
     * Lấy danh sách từ vựng cần ôn tập dựa trên logic Spaced Repetition (nextReviewAt <= Now).
     *
     */
    public List<VocabularyResponse> getReviewList(Long userId) {
        // Lấy thời điểm hiện tại để so sánh
        LocalDateTime now = LocalDateTime.now(); 
        
        // Truy vấn repository cho các bản ghi đến hạn ôn tập
        List<UserVocabularyProgress> dueForReview = userVocabProgressRepository.findDueForReview(userId, now);

        return dueForReview.stream()
                .map(progress -> toResponse(progress.getVocabulary()))
                .collect(Collectors.toList());
    }

    /**
     * Phương thức bổ trợ chuyển đổi Entity sang Response DTO rút gọn.
     *
     */
    private VocabularyResponse toResponse(Vocabulary v) {
        return VocabularyResponse.builder()
                .id(v.getId()) // Sẽ hiển thị là vocabId trong JSON
                .word(v.getWord())
                .level(v.getLevel())
                .userId(v.getUserId())
                .build();
    }
}
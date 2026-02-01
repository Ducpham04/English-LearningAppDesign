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
 * Service xử lý logic cho Vocabulary.
 * Đã loại bỏ lessonId để tập trung vào quản lý kho từ vựng chung.
 */
@Service
@RequiredArgsConstructor
public class VocabularyService {

    private final VocabularyRepository repository;
    private final UserVocabularyProgressRepository userVocabProgressRepository;

    /**
     * Lấy danh sách từ vựng tích hợp Search, Filter và Phân trang.
     */
    public Page<VocabularyResponse> getAll(String word, String level, Pageable pageable) {
        return repository.findWithFilters(word, level, pageable)
                .map(this::toResponse);
    }

    /**
     * Tạo mới từ vựng vào kho chung.
     * Đã lược bỏ toàn bộ logic liên kết với Lesson để tránh lỗi 500 khi test.
     */
    @Transactional
    public VocabularyResponse create(VocabularyRequest req) {
        Vocabulary v = Vocabulary.builder()
                .word(req.getWord())
                .phonetic(req.getPhonetic())
                .definition(req.getDefinition())
                .example(req.getExample())
                .level(req.getLevel())
                .build();
        
        Vocabulary savedVocab = repository.save(v);
        
        return toResponse(savedVocab);
    }

    /**
     * Thuật toán Spaced Repetition (Giữ lại cho Student ôn tập sau này).
     */
    public List<VocabularyResponse> getReviewList(Long userId) {
        LocalDateTime now = LocalDateTime.now(); 
        List<UserVocabularyProgress> dueForReview = userVocabProgressRepository.findDueForReview(userId, now);

        return dueForReview.stream()
                .map(progress -> toResponse(progress.getVocabulary()))
                .collect(Collectors.toList());
    }

    private VocabularyResponse toResponse(Vocabulary v) {
        return VocabularyResponse.builder()
                .id(v.getId()) 
                .word(v.getWord())
                .phonetic(v.getPhonetic())
                .definition(v.getDefinition())
                .example(v.getExample())
                .level(v.getLevel())
                .build();
    }
}
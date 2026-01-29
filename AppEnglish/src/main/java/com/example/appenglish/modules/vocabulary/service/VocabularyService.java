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
 * Service xử lý logic cho Vocabulary - Đã lược bỏ userId.
 */
@Service
@RequiredArgsConstructor
public class VocabularyService {

    private final VocabularyRepository repository;
    private final UserVocabularyProgressRepository userVocabProgressRepository;

    /**
     * Lấy danh sách từ vựng tích hợp Search (word), Filter (level) và Phân trang.
     * Tự động hiển thị ID (vocabId) trong kết quả trả về.
     */
    public Page<VocabularyResponse> getAll(String word, String level, Pageable pageable) {
    // Chỉ nhận 3 tham số
    return repository.findWithFilters(word, level, pageable)
            .map(this::toResponse);
}

    /**
     * Tạo mới từ vựng. 
     * ID được sinh tự động bởi Database (1, 2, 3...)
     */
    @Transactional
    public VocabularyResponse create(VocabularyRequest req) {
        Vocabulary v = Vocabulary.builder()
                .word(req.getWord())
                .phonetic(req.getPhonetic())
                .definition(req.getDefinition())
                .example(req.getExample())
                .level(req.getLevel())
                // Tuyệt đối không gọi .userId() ở đây
                .build();
        
        return toResponse(repository.save(v));
    }

    /**
     * Lấy danh sách từ vựng cần ôn tập.
     */
    public List<VocabularyResponse> getReviewList(Long userId) {
        LocalDateTime now = LocalDateTime.now(); 
        List<UserVocabularyProgress> dueForReview = userVocabProgressRepository.findDueForReview(userId, now);

        return dueForReview.stream()
                .map(progress -> toResponse(progress.getVocabulary()))
                .collect(Collectors.toList());
    }

    /**
     * Chuyển đổi Entity sang Response.
     * ID lấy từ BaseEntity (tự tăng) sẽ được map vào DTO.
     */
    private VocabularyResponse toResponse(Vocabulary v) {
        return VocabularyResponse.builder()
                .id(v.getId()) // Trả về ID tự tăng (VocabId)
                .word(v.getWord())
                .phonetic(v.getPhonetic())
                .definition(v.getDefinition())
                .example(v.getExample())
                .level(v.getLevel())
                // Đã xóa userId ở đây
                .build();
    }
}
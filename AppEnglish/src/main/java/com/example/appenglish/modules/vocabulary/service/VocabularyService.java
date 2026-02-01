package com.example.appenglish.modules.vocabulary.service;

import com.example.appenglish.entity.UserVocabularyProgress;
import com.example.appenglish.entity.Vocabulary;
import com.example.appenglish.enums.LearningStatus;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VocabularyService {

    private final VocabularyRepository repository;
    private final UserVocabularyProgressRepository userVocabProgressRepository;

    /**
     * Format 7.1: Lấy danh sách từ vựng kèm Stats và Pagination.
     * Đã bỏ fix cứng userId = 1L.
     */
    public VocabularyResponse.VocabularyListWrapper getAllCustomFormat(
            Long sessionId, 
            Boolean learned, 
            String search, 
            Pageable pageable, 
            Long userId) {
        
        Page<Vocabulary> vocabPage = repository.findWithFilters(search, null, pageable); 
        
        List<VocabularyResponse> words = vocabPage.getContent().stream()
                .map(v -> toResponseWithUserContext(v, userId))
                .collect(Collectors.toList());

        long total = repository.count();
        
        // Tính toán thống kê dựa trên userId thật, nếu chưa đăng nhập thì mặc định là 0
        long learnedCount = 0;
        long learningCount = 0;
        
        if (userId != null) {
            learnedCount = userVocabProgressRepository.countByUserIdAndStatus(userId, LearningStatus.COMPLETED);
            learningCount = userVocabProgressRepository.countByUserIdAndStatus(userId, LearningStatus.STARTED);
        }

        return VocabularyResponse.VocabularyListWrapper.builder()
                .words(words)
                .pagination(VocabularyResponse.PaginationData.builder()
                        .page(vocabPage.getNumber() + 1)
                        .limit(vocabPage.getSize())
                        .total(vocabPage.getTotalElements())
                        .totalPages(vocabPage.getTotalPages())
                        .build())
                .stats(VocabularyResponse.StatsData.builder()
                        .total(total)
                        .learned(learnedCount)
                        .learning(learningCount)
                        .notStarted(total - learnedCount - learningCount)
                        .build())
                .build();
    }

    @Transactional
    public VocabularyResponse create(VocabularyRequest req) {
        Vocabulary v = Vocabulary.builder()
                .word(req.getWord())
                .phonetic(req.getPronunciation()) 
                .definition(req.getMeaning())    
                .example(req.getExample())
                .level(req.getLevel())
                .build();
        
        Vocabulary savedVocab = repository.save(v);
        return toResponse(savedVocab);
    }

    /**
     * Format 7.2: Lấy danh sách ôn tập (Review List) cho userId động.
     */
    public VocabularyResponse.VocabularyReviewWrapper getReviewList(Long userId) {
        LocalDateTime now = LocalDateTime.now(); 
        List<UserVocabularyProgress> dueForReview = userVocabProgressRepository.findDueForReview(userId, now);

        List<VocabularyResponse> words = dueForReview.stream()
                .map(progress -> {
                    VocabularyResponse res = toResponseWithUserContext(progress.getVocabulary(), userId);
                    if (progress.getNextReviewAt() != null) {
                        res.setNextReviewDate(progress.getNextReviewAt().toString());
                    }
                    return res;
                })
                .collect(Collectors.toList());

        return VocabularyResponse.VocabularyReviewWrapper.builder()
                .words(words)
                .total(words.size())
                .build();
    }

    private VocabularyResponse toResponse(Vocabulary v) {
        return VocabularyResponse.builder()
                .id(v.getId())
                .word(v.getWord())
                .meaning(v.getDefinition())
                .pronunciation(v.getPhonetic())
                .example(v.getExample())
                .level(v.getLevel())
                .build();
    }

    /**
     * Helper: Map dữ liệu từ vựng kèm theo ngữ cảnh người dùng hiện tại.
     * Xử lý an toàn nếu userId = null.
     */
    private VocabularyResponse toResponseWithUserContext(Vocabulary v, Long userId) {
        // Nếu không có userId (chưa đăng nhập), progress luôn là rỗng
        Optional<UserVocabularyProgress> progressOpt = (userId != null) 
                ? userVocabProgressRepository.findByUserIdAndVocabularyId(userId, v.getId())
                : Optional.empty();
        
        return VocabularyResponse.builder()
                .id(v.getId())
                .word(v.getWord())
                .meaning(v.getDefinition())
                .pronunciation(v.getPhonetic())
                .example(v.getExample())
                .level(v.getLevel())
                .learned(progressOpt.isPresent() && progressOpt.get().getStatus() == LearningStatus.COMPLETED)
                .reviewCount(progressOpt.map(UserVocabularyProgress::getReviewCount).orElse(0))
                .mastery(progressOpt.map(p -> {
                    Integer count = p.getReviewCount();
                    return (count != null) ? count * 20 : 0;
                }).orElse(0))
                .build();
    }
}
package com.example.appenglish.modules.vocabulary.service;

import com.example.appenglish.entity.Lesson;
import com.example.appenglish.entity.LessonVocabulary;
import com.example.appenglish.entity.UserVocabularyProgress;
import com.example.appenglish.entity.Vocabulary;
import com.example.appenglish.modules.vocabulary.dto.VocabularyRequest;
import com.example.appenglish.modules.vocabulary.dto.VocabularyResponse;
import com.example.appenglish.repository.LessonRepository;
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
 * Hỗ trợ Teacher tạo từ vựng và gắn vào Lesson để Student ôn tập.
 */
@Service
@RequiredArgsConstructor
public class VocabularyService {

    private final VocabularyRepository repository;
    private final UserVocabularyProgressRepository userVocabProgressRepository;
    private final LessonRepository lessonRepository;

    /**
     * Lấy danh sách từ vựng tích hợp Search (word), Filter (level) và Phân trang.
     */
    public Page<VocabularyResponse> getAll(String word, String level, Pageable pageable) {
        return repository.findWithFilters(word, level, pageable)
                .map(this::toResponse);
    }

    /**
     * Tạo mới từ vựng và tự động liên kết với Bài học (Lesson).
     * Khi Teacher post từ vựng kèm lessonId, từ này sẽ xuất hiện trong luồng học của Student.
     */
    @Transactional
    public VocabularyResponse create(VocabularyRequest req) {
        // 1. Tạo và lưu Entity Vocabulary
        Vocabulary v = Vocabulary.builder()
                .word(req.getWord())
                .phonetic(req.getPhonetic())
                .definition(req.getDefinition())
                .example(req.getExample())
                .level(req.getLevel())
                .build();
        
        Vocabulary savedVocab = repository.save(v);

        // 2. Logic kết nối: Nếu có lessonId, tạo bản ghi trong bảng liên kết LessonVocabulary
        if (req.getLessonId() != null) {
            Lesson lesson = lessonRepository.findById(req.getLessonId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học với ID: " + req.getLessonId()));
            
            // Khởi tạo mối quan hệ Lesson - Vocabulary
            LessonVocabulary link = new LessonVocabulary();
            link.setLesson(lesson);
            link.setVocabulary(savedVocab);
            
            // Lưu liên kết (Đảm bảo thực thể Lesson có Cascade hoặc lưu qua Repository liên kết nếu có)
            lesson.getLessonVocabularies().add(link);
            lessonRepository.save(lesson);
        }
        
        return toResponse(savedVocab);
    }

    /**
     * Lấy danh sách từ vựng cần ôn tập dựa trên thuật toán Spaced Repetition (Lặp lại ngắt quãng).
     * Student sẽ học những từ mà Teacher đã gán vào tiến trình của họ.
     */
    public List<VocabularyResponse> getReviewList(Long userId) {
        LocalDateTime now = LocalDateTime.now(); 
        // Tìm các từ vựng đến hạn ôn tập của User cụ thể
        List<UserVocabularyProgress> dueForReview = userVocabProgressRepository.findDueForReview(userId, now);

        return dueForReview.stream()
                .map(progress -> toResponse(progress.getVocabulary()))
                .collect(Collectors.toList());
    }

    /**
     * Chuyển đổi Entity sang Response DTO.
     */
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
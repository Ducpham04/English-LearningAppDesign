package com.example.appenglish.modules.vocabulary.service;

import com.example.appenglish.entity.Vocabulary;
import com.example.appenglish.modules.vocabulary.dto.VocabularyRequest;
import com.example.appenglish.modules.vocabulary.dto.VocabularyResponse;
import com.example.appenglish.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Service cho Vocabulary.
 */
@Service
@RequiredArgsConstructor
public class VocabularyService {

    private final VocabularyRepository vocabularyRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Tìm tất cả từ vựng với phân trang.
     */
    public Page<VocabularyResponse> getAllVocabularies(Pageable pageable) {
        return vocabularyRepository.findAll(pageable)
                .map(this::toResponse);
    }

    /**
     * Tìm kiếm từ vựng theo từ khóa.
     */
    public Page<VocabularyResponse> searchVocabularies(String searchTerm, Pageable pageable) {
        return vocabularyRepository.findByWordContainingIgnoreCase(searchTerm, pageable)
                .map(this::toResponse);
    }

    /**
     * Tìm từ vựng theo từ chính xác.
     */
    public Optional<VocabularyResponse> findByWord(String word) {
        return vocabularyRepository.findByWordIgnoreCase(word)
                .map(this::toResponse);
    }

    /**
     * Tạo từ vựng mới.
     */
    public VocabularyResponse createVocabulary(VocabularyRequest request) {
        Vocabulary vocabulary = Vocabulary.builder()
                .word(request.getWord().toLowerCase().trim())
                .phonetic(request.getPhonetic())
                .audioUrl(request.getAudioUrl())
                .meanings(request.getMeanings())
                .tags(request.getTags())
                .build();
        
        Vocabulary saved = vocabularyRepository.save(vocabulary);
        return toResponse(saved);
    }

    /**
     * Cập nhật từ vựng theo ID.
     */
    public VocabularyResponse updateVocabulary(Long id, VocabularyRequest request) {
        Optional<Vocabulary> existingVocab = vocabularyRepository.findById(id);
        
        if (existingVocab.isPresent()) {
            Vocabulary vocabulary = existingVocab.get();
            vocabulary.setWord(request.getWord().toLowerCase().trim());
            vocabulary.setPhonetic(request.getPhonetic());
            vocabulary.setAudioUrl(request.getAudioUrl());
            vocabulary.setMeanings(request.getMeanings());
            vocabulary.setTags(request.getTags());
            
            Vocabulary updated = vocabularyRepository.save(vocabulary);
            return toResponse(updated);
        }
        throw new RuntimeException("Vocabulary not found with id: " + id);
    }

    /**
     * Xóa từ vựng theo ID.
     */
    public void deleteVocabulary(Long id) {
        if (!vocabularyRepository.existsById(id)) {
            throw new RuntimeException("Vocabulary not found with id: " + id);
        }
        vocabularyRepository.deleteById(id);
    }

    /**
     * Lấy chi tiết từ vựng theo ID.
     */
    public VocabularyResponse getVocabularyById(Long id) {
        Optional<Vocabulary> vocabulary = vocabularyRepository.findById(id);
        
        if (vocabulary.isPresent()) {
            return toResponse(vocabulary.get());
        }
        throw new RuntimeException("Vocabulary not found with id: " + id);
    }

    /**
     * Convert Entity sang Response DTO.
     */
    private VocabularyResponse toResponse(Vocabulary vocabulary) {
        return VocabularyResponse.builder()
                .id(vocabulary.getId())
                .word(vocabulary.getWord())
                .phonetic(vocabulary.getPhonetic())
                .audioUrl(vocabulary.getAudioUrl())
                .meanings(vocabulary.getMeanings())
                .tags(vocabulary.getTags())
                .createdAt(vocabulary.getCreatedAt() != null ? 
                        vocabulary.getCreatedAt().format(DATE_FORMATTER) : null)
                .build();
    }
}

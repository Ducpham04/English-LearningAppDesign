package com.example.appenglish.modules.vocabulary.service;

import com.example.appenglish.entity.Vocabulary;
import com.example.appenglish.modules.vocabulary.dto.VocabularyDto;
import com.example.appenglish.modules.vocabulary.dto.VocabularyRequest;
import com.example.appenglish.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VocabularyService {

    private final VocabularyRepository vocabularyRepository;

    @Transactional(readOnly = true)
    public List<VocabularyDto> getAllVocabularies() {
        return vocabularyRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VocabularyDto getVocabularyById(Long id) {
        Vocabulary vocabulary = vocabularyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vocabulary not found with id: " + id));
        return mapToDto(vocabulary);
    }

    @Transactional(readOnly = true)
    public List<VocabularyDto> searchVocabularies(String word) {
        return vocabularyRepository.findByWordContainingIgnoreCase(word).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public VocabularyDto createVocabulary(VocabularyRequest request) {
        Vocabulary vocabulary = Vocabulary.builder()
                .word(request.getWord())
                .phonetic(request.getPhonetic())
                .audioUrl(request.getAudioUrl())
                .meanings(request.getMeanings())
                .tags(request.getTags())
                .build();

        Vocabulary saved = vocabularyRepository.save(vocabulary);
        return mapToDto(saved);
    }

    @Transactional
    public VocabularyDto updateVocabulary(Long id, VocabularyRequest request) {
        Vocabulary vocabulary = vocabularyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vocabulary not found with id: " + id));

        if (request.getWord() != null) {
            vocabulary.setWord(request.getWord());
        }
        if (request.getPhonetic() != null) {
            vocabulary.setPhonetic(request.getPhonetic());
        }
        if (request.getAudioUrl() != null) {
            vocabulary.setAudioUrl(request.getAudioUrl());
        }
        if (request.getMeanings() != null) {
            vocabulary.setMeanings(request.getMeanings());
        }
        if (request.getTags() != null) {
            vocabulary.setTags(request.getTags());
        }

        Vocabulary saved = vocabularyRepository.save(vocabulary);
        return mapToDto(saved);
    }

    @Transactional
    public void deleteVocabulary(Long id) {
        if (!vocabularyRepository.existsById(id)) {
            throw new RuntimeException("Vocabulary not found with id: " + id);
        }
        vocabularyRepository.deleteById(id);
    }

    private VocabularyDto mapToDto(Vocabulary vocabulary) {
        return VocabularyDto.builder()
                .id(vocabulary.getId())
                .word(vocabulary.getWord())
                .phonetic(vocabulary.getPhonetic())
                .audioUrl(vocabulary.getAudioUrl())
                .meanings(vocabulary.getMeanings())
                .tags(vocabulary.getTags())
                .build();
    }
}

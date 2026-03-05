package com.example.AppEnglish.modules.vocabulary.service.impl;

import com.example.AppEnglish.entity.UserVocabularyProgress;
import com.example.AppEnglish.modules.vocabulary.dto.ReviewListResponse;
import com.example.AppEnglish.modules.vocabulary.dto.ReviewVocabularyDTO;
import com.example.AppEnglish.modules.vocabulary.service.SpacedRepetitionService;
import com.example.AppEnglish.repository.UserVocabularyProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SpacedRepetitionServiceImpl implements SpacedRepetitionService {

    private final UserVocabularyProgressRepository progressRepository;

    @Override
    public ReviewListResponse getWordsToReview(Long userId, int limit) {
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, limit);

        List<UserVocabularyProgress> dueWords = progressRepository.findDueForReview(userId, now, pageable);

        List<ReviewVocabularyDTO> words = dueWords.stream()
                .map(ReviewVocabularyDTO::fromEntity)
                .collect(Collectors.toList());

        return ReviewListResponse.builder()
                .words(words)
                .total(words.size())
                .build();
    }
}

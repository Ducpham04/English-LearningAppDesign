package com.example.AppEnglish.modules.vocabulary.service.impl;

import com.example.AppEnglish.enums.LearningStatus;
import com.example.AppEnglish.entity.UserVocabularyProgress;
import com.example.AppEnglish.entity.Vocabulary;
import com.example.AppEnglish.modules.vocabulary.dto.VocabularyDTO;
import com.example.AppEnglish.modules.vocabulary.service.VocabularyService;
import com.example.AppEnglish.modules.vocabulary.dto.VocabularyListResponse;
import com.example.AppEnglish.repository.UserVocabularyProgressRepository;
import com.example.AppEnglish.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VocabularyServiceImpl implements VocabularyService {

        private final VocabularyRepository vocabularyRepository;
        private final UserVocabularyProgressRepository progressRepository;

        @Override
        public VocabularyListResponse searchVocabulary(String search, Long sessionId,
                        Boolean learned, Pageable pageable,
                        Long currentUserId) {

                String keyword = (search != null && !search.isBlank()) ? search : null;
                Page<Vocabulary> vocabPage = vocabularyRepository.searchByKeywordAndSession(keyword, sessionId,
                                pageable);

                Page<VocabularyDTO> wordsPage = vocabPage.map(v -> {
                        UserVocabularyProgress progress = currentUserId != null
                                        ? progressRepository.findByUserIdAndVocabId(currentUserId, v.getId())
                                                        .orElse(null)
                                        : null;
                        return VocabularyDTO.fromEntity(v, progress);
                });

                // Filter by learned status in-memory (depends on user progress)
                if (learned != null && currentUserId != null) {
                        List<VocabularyDTO> filtered = wordsPage.getContent().stream()
                                        .filter(dto -> learned.equals(dto.getLearned()))
                                        .collect(Collectors.toList());
                        wordsPage = new PageImpl<>(filtered, pageable, filtered.size());
                }

                long totalVocab = vocabularyRepository.count();
                long learnedCount = progressRepository.countByUser_IdAndStatus(currentUserId, LearningStatus.MASTERED);
                long learningCount = progressRepository.countByUser_IdAndStatus(currentUserId, LearningStatus.LEARNING)
                                + progressRepository.countByUser_IdAndStatus(currentUserId, LearningStatus.REVIEW);
                long notStartedCount = totalVocab - learnedCount - learningCount;

                VocabularyListResponse.VocabStatsDTO stats = VocabularyListResponse.VocabStatsDTO.builder()
                                .total(totalVocab)
                                .learned(learnedCount)
                                .learning(learningCount)
                                .notStarted(Math.max(0, notStartedCount))
                                .build();

                return VocabularyListResponse.builder()
                                .words(wordsPage)
                                .stats(stats)
                                .build();
        }
}

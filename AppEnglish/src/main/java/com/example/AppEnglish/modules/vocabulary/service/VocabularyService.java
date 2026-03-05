package com.example.AppEnglish.modules.vocabulary.service;

import com.example.AppEnglish.modules.vocabulary.dto.VocabularyListResponse;
import org.springframework.data.domain.Pageable;

public interface VocabularyService {

    VocabularyListResponse searchVocabulary(String search, Long sessionId,
            Boolean learned, Pageable pageable,
            Long currentUserId);
}

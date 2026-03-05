package com.example.AppEnglish.modules.vocabulary.service;

import com.example.AppEnglish.modules.vocabulary.dto.ReviewListResponse;

public interface SpacedRepetitionService {

    ReviewListResponse getWordsToReview(Long userId, int limit);
}

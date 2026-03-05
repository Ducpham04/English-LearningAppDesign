package com.example.AppEnglish.modules.vocabulary.controller;

import com.example.AppEnglish.common.dto.BaseResponse;
import com.example.AppEnglish.entity.User;
import com.example.AppEnglish.modules.vocabulary.dto.ReviewListResponse;
import com.example.AppEnglish.modules.vocabulary.dto.VocabularyListResponse;
import com.example.AppEnglish.modules.vocabulary.service.SpacedRepetitionService;
import com.example.AppEnglish.modules.vocabulary.service.VocabularyService;
import com.example.AppEnglish.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/vocabulary")
@RequiredArgsConstructor
@Tag(name = "Vocabulary", description = "Vocabulary & Spaced Repetition APIs")
public class VocabularyController {

    private final VocabularyService vocabularyService;
    private final SpacedRepetitionService spacedRepetitionService;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Get all vocabulary", description = "Search, filter, and paginate vocabulary bank")
    public ResponseEntity<BaseResponse<VocabularyListResponse>> getAllVocabulary(
            @Parameter(description = "Filter by session ID") @RequestParam(required = false) Long sessionId,

            @Parameter(description = "Filter by learned status: true or false") @RequestParam(required = false) Boolean learned,

            @Parameter(description = "Search by word") @RequestParam(required = false) String search,

            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "50") int limit,

            Authentication authentication,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page, limit, Sort.by("word").ascending());
        Long currentUserId = getCurrentUserId(authentication);
        VocabularyListResponse response = vocabularyService.searchVocabulary(
                search, sessionId, learned, pageable, currentUserId);

        return ResponseEntity.ok(BaseResponse.ok(response, "Vocabulary retrieved", request.getRequestURI()));
    }

    @GetMapping("/review")
    @Operation(summary = "Get words to review", description = "Get vocabulary due for spaced repetition review")
    public ResponseEntity<BaseResponse<ReviewListResponse>> getWordsToReview(
            @RequestParam(defaultValue = "20") int limit,
            Authentication authentication,
            HttpServletRequest request) {

        Long currentUserId = getCurrentUserId(authentication);
        if (currentUserId == null) {
            return ResponseEntity.status(401)
                    .body(BaseResponse.error("Authentication required", "UNAUTHORIZED", request.getRequestURI()));
        }

        ReviewListResponse response = spacedRepetitionService.getWordsToReview(currentUserId, limit);

        return ResponseEntity.ok(BaseResponse.ok(response, "Review words retrieved", request.getRequestURI()));
    }

    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return null;
        }
        return userRepository.findByEmail(authentication.getName())
                .map(User::getId)
                .orElse(null);
    }
}

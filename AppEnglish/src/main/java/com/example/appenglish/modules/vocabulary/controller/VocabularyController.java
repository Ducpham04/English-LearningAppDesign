package com.example.appenglish.modules.vocabulary.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.common.utils.SecurityUtils;
import com.example.appenglish.modules.vocabulary.dto.VocabularyRequest;
import com.example.appenglish.modules.vocabulary.dto.VocabularyResponse;
import com.example.appenglish.modules.vocabulary.service.VocabularyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vocabulary")
@RequiredArgsConstructor
@Tag(name = "Vocabulary")
public class VocabularyController {

    private final VocabularyService service;

    @GetMapping
    @Operation(summary = "Get all vocabularies")
    public ResponseEntity<BaseResponse<VocabularyResponse.VocabularyListWrapper>> getVocabularies(
            @RequestParam(required = false) Long sessionId,
            @RequestParam(required = false) Boolean learned,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int limit,
            HttpServletRequest request) {

        Long userId = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal().toString())) {
            userId = SecurityUtils.getCurrentUserId();
        }
        
        var data = service.getAllCustomFormat(sessionId, learned, search, PageRequest.of(page - 1, limit), userId);

        return ResponseEntity.ok(
            BaseResponse.ok(data, "Fetch vocabularies successfully", request.getRequestURI())
        );
    }

    @GetMapping("/review")
    @Operation(summary = "Get vocabulary review list")
    public ResponseEntity<BaseResponse<VocabularyResponse.VocabularyReviewWrapper>> getReviewList(
            @RequestParam(defaultValue = "20") int limit,
            HttpServletRequest request) {
        
        Long userId = SecurityUtils.getCurrentUserId();
        var reviewData = service.getReviewList(userId);
        
        if (reviewData.getWords() != null && reviewData.getWords().size() > limit) {
            reviewData.setWords(reviewData.getWords().subList(0, limit));
        }
        
        return ResponseEntity.ok(
            BaseResponse.ok(reviewData, "Fetch review list successfully", request.getRequestURI())
        );
    }

    @PostMapping
    @Operation(summary = "Create new vocabulary")
    public ResponseEntity<BaseResponse<VocabularyResponse>> create(
            @Valid @RequestBody VocabularyRequest req,
            HttpServletRequest request) {
        
        var data = service.create(req);
        
        return ResponseEntity.ok(
            BaseResponse.ok(data, "Vocabulary created successfully", request.getRequestURI())
        );
    }
}
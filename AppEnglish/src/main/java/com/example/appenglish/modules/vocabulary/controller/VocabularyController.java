package com.example.appenglish.modules.vocabulary.controller;

import com.example.appenglish.entity.User;
import com.example.appenglish.modules.vocabulary.dto.VocabularyRequest;
import com.example.appenglish.modules.vocabulary.dto.VocabularyResponse;
import com.example.appenglish.modules.vocabulary.service.VocabularyService;
import com.example.appenglish.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vocabulary")
@RequiredArgsConstructor
@Tag(name = "Vocabulary", description = "Quản lý kho từ vựng và tiến trình học tập")
public class VocabularyController {

    private final VocabularyService service;
    private final UserRepository userRepository;

    /**
     * Wrapper cho format 7.1 (Get All)
     */
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class VocabularyDataResponse {
        private boolean success;
        private VocabularyResponse.VocabularyListWrapper data;
    }

    /**
     * Wrapper cho format 7.2 (Review List)
     */
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class VocabularyReviewListResponse {
        private boolean success;
        private VocabularyResponse.VocabularyReviewWrapper data;
    }

    /**
     * Helper: Lấy User ID từ Authentication (Email trong Token)
     */
    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null) return null;
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found for email: " + email));
    }

    @GetMapping
    @Operation(summary = "7.1 Get All Vocabularies", description = "Lấy danh sách từ vựng kèm thống kê cá nhân (nếu đã đăng nhập)")
    public ResponseEntity<VocabularyDataResponse> getVocabularies(
            Authentication authentication,
            @RequestParam(required = false) Long sessionId,
            @RequestParam(required = false) Boolean learned,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int limit) {

        // Lấy ID động, nếu chưa login thì trả về null (Service cần xử lý userId null)
        Long userId = (authentication != null) ? getCurrentUserId(authentication) : null;
        
        var data = service.getAllCustomFormat(sessionId, learned, search, PageRequest.of(page - 1, limit), userId);

        return ResponseEntity.ok(VocabularyDataResponse.builder()
                .success(true)
                .data(data)
                .build());
    }

    @GetMapping("/review")
    @Operation(summary = "7.2 Get Review List", description = "Lấy danh sách các từ cần ôn tập của người dùng hiện tại")
    public ResponseEntity<VocabularyReviewListResponse> getReviewList(
            Authentication authentication,
            @RequestParam(defaultValue = "20") int limit) {
        
        Long userId = getCurrentUserId(authentication);
        var reviewData = service.getReviewList(userId);
        
        if (reviewData.getWords().size() > limit) {
            reviewData.setWords(reviewData.getWords().subList(0, limit));
        }
        
        return ResponseEntity.ok(VocabularyReviewListResponse.builder()
                .success(true)
                .data(reviewData)
                .build());
    }

    @PostMapping
    @Operation(summary = "7.3 Create Vocabulary", description = "Yêu cầu đăng nhập để tạo từ vựng mới")
    public ResponseEntity<VocabularyResponse> post(
            @Valid @RequestBody VocabularyRequest req) {
        return ResponseEntity.ok(service.create(req));
    }
}
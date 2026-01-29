package com.example.appenglish.modules.vocabulary.controller;

import com.example.appenglish.modules.vocabulary.dto.VocabularyRequest;
import com.example.appenglish.modules.vocabulary.dto.VocabularyResponse;
import com.example.appenglish.modules.vocabulary.service.VocabularyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vocabularies")
@RequiredArgsConstructor
@Tag(name = "Vocabulary")
public class VocabularyController {

    private final VocabularyService service;

    /**
     * GET: Lấy danh sách từ vựng.
     * Tích hợp: Tìm kiếm theo từ (word), Lọc theo độ khó (level), Lọc theo người dùng (userId) và Phân trang.
     */
    @GetMapping
    @Operation()
    public ResponseEntity<Page<VocabularyResponse>> get(
            @RequestParam(required = false) String word,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) Long userId,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(service.getAll(word, level, userId, pageable));
    }

    /**
     * GET: Lấy danh sách từ vựng cần ôn tập.
     * Logic: Tìm các từ có nextReviewAt <= thời điểm hiện tại.
     */
    @GetMapping("/review")
    @Operation()
    public ResponseEntity<List<VocabularyResponse>> getReviewList(@RequestParam Long userId) {
        // Gọi method getReviewList từ service đã thống nhất ở bước trước
        List<VocabularyResponse> reviewList = service.getReviewList(userId);
        
        if (reviewList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reviewList);
    }

    /**
     * POST: Tạo mới từ vựng.
     * Chỉ bao gồm các thành phần chính: word, level, userId.
     */
    @PostMapping
    @Operation()
    public ResponseEntity<VocabularyResponse> post(@RequestBody VocabularyRequest req) {
        return ResponseEntity.ok(service.create(req));
    }
}
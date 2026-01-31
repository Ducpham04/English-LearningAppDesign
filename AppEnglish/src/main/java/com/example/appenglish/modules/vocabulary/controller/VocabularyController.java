package com.example.appenglish.modules.vocabulary.controller;

import com.example.appenglish.modules.vocabulary.dto.VocabularyRequest;
import com.example.appenglish.modules.vocabulary.dto.VocabularyResponse;
import com.example.appenglish.modules.vocabulary.service.VocabularyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vocabularies")
@RequiredArgsConstructor
@Tag(name = "Vocabulary", description = "API quản lý từ vựng")
public class VocabularyController {

    private final VocabularyService service;

    /**
     * GET: Lấy danh sách từ vựng.
     * Tích hợp: Tìm kiếm theo từ (word), Lọc theo độ khó (level) và Phân trang.
     */
    @GetMapping
    @Operation(summary = "Lấy danh sách từ vựng", description = "Hỗ trợ tìm kiếm, lọc theo level và phân trang")
    public ResponseEntity<Page<VocabularyResponse>> getVocabularies(
            @RequestParam(required = false) String word,
            @RequestParam(required = false) String level,
            @PageableDefault(size = 10) Pageable pageable) {

        // Đã sửa: Chỉ truyền 3 tham số (word, level, pageable)
        Page<VocabularyResponse> result = service.getAll(word, level, pageable);

        return ResponseEntity.ok(result);
    }

    /**
     * GET: Lấy danh sách từ vựng cần ôn tập.
     * Logic: Tìm các từ có nextReviewAt <= thời điểm hiện tại.
     */
    @GetMapping("/review")
    @Operation(summary = "Lấy danh sách cần ôn tập", description = "Trả về các từ vựng đã đến hạn ôn tập của người dùng")
    public ResponseEntity<List<VocabularyResponse>> getReviewList(@RequestParam Long userId) {
        List<VocabularyResponse> reviewList = service.getReviewList(userId);

        if (reviewList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reviewList);
    }

    /**
     * POST: Tạo mới từ vựng.
     * Chỉ bao gồm các thành phần chính: word, level... (Không gắn với userId vì đây là từ điển chung)
     */
    @PostMapping
    @Operation(summary = "Tạo từ vựng mới", description = "Thêm từ mới vào từ điển chung")
    public ResponseEntity<VocabularyResponse> post(@RequestBody VocabularyRequest req) {
        return ResponseEntity.ok(service.create(req));
    }
}
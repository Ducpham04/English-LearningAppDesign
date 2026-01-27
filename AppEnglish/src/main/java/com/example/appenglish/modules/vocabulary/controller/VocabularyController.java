package com.example.appenglish.modules.vocabulary.controller;

import com.example.appenglish.modules.vocabulary.dto.VocabularyRequest;
import com.example.appenglish.modules.vocabulary.dto.VocabularyResponse;
import com.example.appenglish.modules.vocabulary.service.VocabularyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller cho Vocabulary API.
 */
@RestController
@RequestMapping("/api/vocabularies")
@RequiredArgsConstructor
@Tag(name = "Vocabulary")
public class VocabularyController {

    private final VocabularyService vocabularyService;

    /**
     * Lấy tất cả từ vựng với phân trang.
     * 
     * @param pageable Phân trang (page, size, sort)
     * @return Danh sách từ vựng có phân trang
     */
    @GetMapping
    @Operation(    )
    public ResponseEntity<Page<VocabularyResponse>> getAllVocabularies(
            @PageableDefault(size = 20, page = 0, sort = "createdAt", direction = Sort.Direction.DESC)
            @Parameter(description = "Phân trang (page=0, size=20, sort=createdAt,desc)")
            Pageable pageable) {
        return ResponseEntity.ok(vocabularyService.getAllVocabularies(pageable));
    }

    /**
     * Tìm kiếm từ vựng theo từ khóa.
     * 
     * @param searchTerm Từ khóa tìm kiếm
     * @param pageable Phân trang
     * @return Danh sách từ vựng phù hợp
     */
    @GetMapping("/search")
    @Operation(    )
    public ResponseEntity<Page<VocabularyResponse>> searchVocabularies(
            @RequestParam(value = "q", required = true)
            @Parameter(description = "Từ khóa tìm kiếm", example = "hello")
            String searchTerm,
            
            @PageableDefault(size = 20, page = 0, sort = "createdAt", direction = Sort.Direction.DESC)
            @Parameter(description = "Phân trang")
            Pageable pageable) {
        return ResponseEntity.ok(vocabularyService.searchVocabularies(searchTerm, pageable));
    }

    /**
     * Lấy các từ vựng cần ôn tập ngay bây giờ (nextReviewAt <= now).
     * 
     * @return Danh sách từ vựng cần ôn tập
     */
    @GetMapping("/review")
    @Operation(    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "204", description = "Không có từ nào cần ôn tập")
    })
    public ResponseEntity<?> getVocabulariesForReview() {
        var reviews = vocabularyService.getVocabulariesForReview();
        
        if (reviews.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reviews);
    }

    /**
     * Lấy từ vựng cần ôn tập của người dùng cụ thể.
     * 
     * @param userId ID người dùng
     * @return Danh sách từ vựng cần ôn tập
     */
    @GetMapping("/review/user/{userId}")
    @Operation(    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "204", description = "Không có từ nào cần ôn tập")
    })
    public ResponseEntity<?> getVocabulariesForReviewByUser(
            @PathVariable
            @Parameter(description = "ID người dùng", example = "1")
            Long userId) {
        var reviews = vocabularyService.getVocabulariesForReviewByUserId(userId);
        
        if (reviews.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reviews);
    }

    /**
     * Tìm từ vựng theo từ chính xác.
     * 
     * @param word Từ cần tìm
     * @return Thông tin chi tiết từ vựng hoặc 404 nếu không tìm thấy
     */
    @GetMapping("/{word}")
    @Operation(    )
    public ResponseEntity<?> getVocabularyByWord(
            @PathVariable
            @Parameter(description = "Từ vựng cần tìm", example = "hello")
            String word) {
        
        Optional<VocabularyResponse> vocabulary = vocabularyService.findByWord(word);
        
        if (vocabulary.isPresent()) {
            return ResponseEntity.ok(vocabulary.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Lấy chi tiết từ vựng theo ID.
     * 
     * @param id ID từ vựng
     * @return Thông tin chi tiết từ vựng
     */
    @GetMapping("/id/{id}")
    @Operation(    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Từ vựng không tồn tại")
    })
    public ResponseEntity<VocabularyResponse> getVocabularyById(
            @PathVariable
            @Parameter(description = "ID từ vựng", example = "1")
            Long id) {
        try {
            VocabularyResponse vocabulary = vocabularyService.getVocabularyById(id);
            return ResponseEntity.ok(vocabulary);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Tạo từ vựng mới.
     * 
     * @param request Thông tin từ vựng
     * @return Từ vựng được tạo
     */
    @PostMapping
    @Operation(    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tạo thành công"),
        @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    public ResponseEntity<VocabularyResponse> createVocabulary(
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.media.Schema(description = "Thông tin từ vựng")
            VocabularyRequest request) {
        VocabularyResponse created = vocabularyService.createVocabulary(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Cập nhật từ vựng theo ID.
     * 
     * @param id ID từ vựng
     * @param request Thông tin cập nhật
     * @return Từ vựng đã cập nhật
     */
    @PutMapping("/{id}")
    @Operation(    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
        @ApiResponse(responseCode = "404", description = "Từ vựng không tồn tại")
    })
    public ResponseEntity<VocabularyResponse> updateVocabulary(
            @PathVariable
            @Parameter(description = "ID từ vựng", example = "1")
            Long id,
            
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.media.Schema(description = "Thông tin cập nhật")
            VocabularyRequest request) {
        try {
            VocabularyResponse updated = vocabularyService.updateVocabulary(id, request);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Xóa từ vựng theo ID.
     * 
     * @param id ID từ vựng
     * @return Thành công hoặc 404
     */
    @DeleteMapping("/{id}")
    @Operation(
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Xóa thành công"),
        @ApiResponse(responseCode = "404", description = "Từ vựng không tồn tại")
    })
    public ResponseEntity<Void> deleteVocabulary(
            @PathVariable
            @Parameter(description = "ID từ vựng", example = "1")
            Long id) {
        try {
            vocabularyService.deleteVocabulary(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }}
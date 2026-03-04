package com.example.appenglish.modules.vocabulary.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.modules.vocabulary.dto.VocabularyDto;
import com.example.appenglish.modules.vocabulary.dto.VocabularyRequest;
import com.example.appenglish.modules.vocabulary.service.VocabularyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/vocabularies")
@RequiredArgsConstructor
public class VocabularyController {

    private final VocabularyService vocabularyService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<VocabularyDto>>> getAllVocabularies(HttpServletRequest httpReq) {
        List<VocabularyDto> vocabularies = vocabularyService.getAllVocabularies();
        return ResponseEntity.ok(
                BaseResponse.ok(vocabularies, "Get all vocabularies successfully", httpReq.getRequestURI())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<VocabularyDto>> getVocabularyById(
            @PathVariable Long id,
            HttpServletRequest httpReq) {
        VocabularyDto vocabulary = vocabularyService.getVocabularyById(id);
        return ResponseEntity.ok(
                BaseResponse.ok(vocabulary, "Get vocabulary successfully", httpReq.getRequestURI())
        );
    }

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<List<VocabularyDto>>> searchVocabularies(
            @RequestParam String word,
            HttpServletRequest httpReq) {
        List<VocabularyDto> vocabularies = vocabularyService.searchVocabularies(word);
        return ResponseEntity.ok(
                BaseResponse.ok(vocabularies, "Search vocabularies successfully", httpReq.getRequestURI())
        );
    }

    @PostMapping
    public ResponseEntity<BaseResponse<VocabularyDto>> createVocabulary(
            @Valid @RequestBody VocabularyRequest request,
            HttpServletRequest httpReq) {
        VocabularyDto vocabulary = vocabularyService.createVocabulary(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.ok(vocabulary, "Vocabulary created successfully", httpReq.getRequestURI()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<VocabularyDto>> updateVocabulary(
            @PathVariable Long id,
            @Valid @RequestBody VocabularyRequest request,
            HttpServletRequest httpReq) {
        VocabularyDto vocabulary = vocabularyService.updateVocabulary(id, request);
        return ResponseEntity.ok(
                BaseResponse.ok(vocabulary, "Vocabulary updated successfully", httpReq.getRequestURI())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<?>> deleteVocabulary(
            @PathVariable Long id,
            HttpServletRequest httpReq) {
        vocabularyService.deleteVocabulary(id);
        return ResponseEntity.ok(
                BaseResponse.ok(null, "Vocabulary deleted successfully", httpReq.getRequestURI())
        );
    }
}

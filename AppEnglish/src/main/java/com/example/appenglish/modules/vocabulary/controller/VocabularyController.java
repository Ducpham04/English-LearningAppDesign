package com.example.appenglish.modules.vocabulary.controller;

import com.example.appenglish.modules.vocabulary.dto.VocabularyRequest;
import com.example.appenglish.modules.vocabulary.dto.VocabularyResponse;
import com.example.appenglish.modules.vocabulary.service.VocabularyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@Tag(name = "Vocabulary")
public class VocabularyController {

    private final VocabularyService service;

    @GetMapping
    public ResponseEntity<Page<VocabularyResponse>> getVocabularies(
  @RequestParam(required = false) String word,
        @RequestParam(required = false) String level,
        @io.swagger.v3.oas.annotations.Parameter(hidden = true) Pageable pageable) { // Ẩn hoàn toàn
    return ResponseEntity.ok(service.getAll(word, level, pageable));
    }

    @GetMapping("/review")
    public ResponseEntity<List<VocabularyResponse>> getReviewList(
            @RequestParam Long userId) {
        
        List<VocabularyResponse> reviewList = service.getReviewList(userId);
        
        if (reviewList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reviewList);
    }


    @PostMapping
    public ResponseEntity<VocabularyResponse> post(@RequestBody VocabularyRequest req) {
        return ResponseEntity.ok(service.create(req));
    }
}
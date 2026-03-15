package com.example.appenglish.modules.vocabulary.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.entity.Vocabulary;
import com.example.appenglish.modules.vocabulary.service.VocabularyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/vocabularies")
@RequiredArgsConstructor
public class VocabularyController {

    private final VocabularyService vocabularyService;

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<List<Vocabulary>>> search(
            @RequestParam("word") String word,
            HttpServletRequest req) {
        List<Vocabulary> list = vocabularyService.search(word);
        return ResponseEntity.ok(BaseResponse.ok(list, "OK", req.getRequestURI()));
    }
}

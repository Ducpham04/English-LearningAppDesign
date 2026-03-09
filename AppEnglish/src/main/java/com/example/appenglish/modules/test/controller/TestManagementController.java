package com.example.appenglish.modules.test.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.modules.test.dto.QuestionTakeDto;
import com.example.appenglish.modules.test.dto.TestCreateRequest;
import com.example.appenglish.modules.test.dto.TestDto;
import com.example.appenglish.modules.test.dto.TestQuestionCreateRequest;
import com.example.appenglish.modules.test.service.TestService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API cho Admin / Teacher: CRUD bài test, thêm/sửa/xóa câu hỏi.
 */
@RestController
@RequestMapping("/v1/tests/manage")
@RequiredArgsConstructor
public class TestManagementController {

    private final TestService testService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<TestDto>>> listAll(HttpServletRequest req) {
        return ResponseEntity.ok(BaseResponse.ok(testService.listAll(), "OK", req.getRequestURI()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<TestDto>> getById(@PathVariable Long id, HttpServletRequest req) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(testService.getById(id), "OK", req.getRequestURI()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(BaseResponse.error(e.getMessage(), "NOT_FOUND", req.getRequestURI()));
        }
    }

    @PostMapping
    public ResponseEntity<BaseResponse<TestDto>> create(@RequestBody TestCreateRequest body, HttpServletRequest req) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(testService.create(body), "Test created", req.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(BaseResponse.error(e.getMessage(), "BAD_REQUEST", req.getRequestURI()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<TestDto>> update(@PathVariable Long id, @RequestBody TestCreateRequest body, HttpServletRequest req) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(testService.update(id, body), "Test updated", req.getRequestURI()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(BaseResponse.error(e.getMessage(), "NOT_FOUND", req.getRequestURI()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id, HttpServletRequest req) {
        try {
            testService.delete(id);
            return ResponseEntity.ok(BaseResponse.ok(null, "Test deleted", req.getRequestURI()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(BaseResponse.error(e.getMessage(), "NOT_FOUND", req.getRequestURI()));
        }
    }

    @PostMapping("/{testId}/questions")
    public ResponseEntity<BaseResponse<QuestionTakeDto>> addQuestion(
            @PathVariable Long testId,
            @RequestBody TestQuestionCreateRequest body,
            HttpServletRequest req) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(testService.addQuestion(testId, body), "Question added", req.getRequestURI()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(BaseResponse.error(e.getMessage(), "BAD_REQUEST", req.getRequestURI()));
        }
    }

    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<BaseResponse<Void>> deleteQuestion(@PathVariable Long questionId, HttpServletRequest req) {
        try {
            testService.deleteQuestion(questionId);
            return ResponseEntity.ok(BaseResponse.ok(null, "Question deleted", req.getRequestURI()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(BaseResponse.error(e.getMessage(), "NOT_FOUND", req.getRequestURI()));
        }
    }
}

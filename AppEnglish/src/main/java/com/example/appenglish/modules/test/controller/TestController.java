package com.example.appenglish.modules.test.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.modules.test.dto.TestDto;
import com.example.appenglish.modules.test.dto.TestRequest;
import com.example.appenglish.modules.test.service.TestService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/tests")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<TestDto>>> getAllTests(HttpServletRequest httpReq) {
        List<TestDto> tests = testService.getAllTests();
        return ResponseEntity.ok(
                BaseResponse.ok(tests, "Get all tests successfully", httpReq.getRequestURI())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<TestDto>> getTestById(
            @PathVariable Long id,
            HttpServletRequest httpReq) {
        TestDto test = testService.getTestById(id);
        return ResponseEntity.ok(
                BaseResponse.ok(test, "Get test successfully", httpReq.getRequestURI())
        );
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<BaseResponse<List<TestDto>>> getTestsByCourse(
            @PathVariable Long courseId,
            HttpServletRequest httpReq) {
        List<TestDto> tests = testService.getTestsByCourse(courseId);
        return ResponseEntity.ok(
                BaseResponse.ok(tests, "Get tests by course successfully", httpReq.getRequestURI())
        );
    }

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<BaseResponse<List<TestDto>>> getTestsByLesson(
            @PathVariable Long lessonId,
            HttpServletRequest httpReq) {
        List<TestDto> tests = testService.getTestsByLesson(lessonId);
        return ResponseEntity.ok(
                BaseResponse.ok(tests, "Get tests by lesson successfully", httpReq.getRequestURI())
        );
    }

    @PostMapping
    public ResponseEntity<BaseResponse<TestDto>> createTest(
            @Valid @RequestBody TestRequest request,
            HttpServletRequest httpReq) {
        TestDto test = testService.createTest(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.ok(test, "Test created successfully", httpReq.getRequestURI()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<TestDto>> updateTest(
            @PathVariable Long id,
            @Valid @RequestBody TestRequest request,
            HttpServletRequest httpReq) {
        TestDto test = testService.updateTest(id, request);
        return ResponseEntity.ok(
                BaseResponse.ok(test, "Test updated successfully", httpReq.getRequestURI())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<?>> deleteTest(
            @PathVariable Long id,
            HttpServletRequest httpReq) {
        testService.deleteTest(id);
        return ResponseEntity.ok(
                BaseResponse.ok(null, "Test deleted successfully", httpReq.getRequestURI())
        );
    }
}

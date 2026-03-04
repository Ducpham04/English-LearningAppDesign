package com.example.appenglish.modules.curriculum.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.modules.curriculum.dto.CurriculumDto;
import com.example.appenglish.modules.curriculum.dto.CurriculumRequest;
import com.example.appenglish.modules.curriculum.service.CurriculumService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/curriculums")
@RequiredArgsConstructor
public class CurriculumController {

    private final CurriculumService curriculumService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<CurriculumDto>>> getAllCurriculums(HttpServletRequest httpReq) {
        List<CurriculumDto> curriculums = curriculumService.getAllCurriculums();
        return ResponseEntity.ok(
                BaseResponse.ok(curriculums, "Get all curriculums successfully", httpReq.getRequestURI())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<CurriculumDto>> getCurriculumById(
            @PathVariable Long id,
            HttpServletRequest httpReq) {
        CurriculumDto curriculum = curriculumService.getCurriculumById(id);
        return ResponseEntity.ok(
                BaseResponse.ok(curriculum, "Get curriculum successfully", httpReq.getRequestURI())
        );
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<BaseResponse<List<CurriculumDto>>> getCurriculumsByTeacher(
            @PathVariable String teacherId,
            HttpServletRequest httpReq) {
        List<CurriculumDto> curriculums = curriculumService.getCurriculumsByTeacher(teacherId);
        return ResponseEntity.ok(
                BaseResponse.ok(curriculums, "Get curriculums by teacher successfully", httpReq.getRequestURI())
        );
    }

    @PostMapping
    public ResponseEntity<BaseResponse<CurriculumDto>> createCurriculum(
            @Valid @RequestBody CurriculumRequest request,
            HttpServletRequest httpReq) {
        CurriculumDto curriculum = curriculumService.createCurriculum(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.ok(curriculum, "Curriculum created successfully", httpReq.getRequestURI()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<CurriculumDto>> updateCurriculum(
            @PathVariable Long id,
            @Valid @RequestBody CurriculumRequest request,
            HttpServletRequest httpReq) {
        CurriculumDto curriculum = curriculumService.updateCurriculum(id, request);
        return ResponseEntity.ok(
                BaseResponse.ok(curriculum, "Curriculum updated successfully", httpReq.getRequestURI())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<?>> deleteCurriculum(
            @PathVariable Long id,
            HttpServletRequest httpReq) {
        curriculumService.deleteCurriculum(id);
        return ResponseEntity.ok(
                BaseResponse.ok(null, "Curriculum deleted successfully", httpReq.getRequestURI())
        );
    }
}

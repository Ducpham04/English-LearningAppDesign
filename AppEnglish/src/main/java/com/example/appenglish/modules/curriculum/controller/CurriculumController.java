package com.example.appenglish.modules.curriculum.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.modules.curriculum.dto.CurriculumCreateRequest;
import com.example.appenglish.modules.curriculum.dto.CurriculumDto;
import com.example.appenglish.modules.curriculum.service.CurriculumService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/curriculums")
@RequiredArgsConstructor
public class CurriculumController {

    private final CurriculumService curriculumService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<CurriculumDto>>> getAll(HttpServletRequest req) {
        return ResponseEntity.ok(BaseResponse.ok(curriculumService.getAll(), "OK", req.getRequestURI()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<CurriculumDto>> getById(@PathVariable Long id, HttpServletRequest req) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(curriculumService.getById(id), "OK", req.getRequestURI()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(BaseResponse.error(e.getMessage(), "NOT_FOUND", req.getRequestURI()));
        }
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<BaseResponse<List<CurriculumDto>>> getByTeacher(@PathVariable String teacherId, HttpServletRequest req) {
        return ResponseEntity.ok(BaseResponse.ok(curriculumService.getByTeacher(teacherId), "OK", req.getRequestURI()));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<CurriculumDto>> create(@RequestBody CurriculumCreateRequest body, HttpServletRequest req) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(curriculumService.create(body), "Curriculum created", req.getRequestURI()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(BaseResponse.error(e.getMessage(), "BAD_REQUEST", req.getRequestURI()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id, HttpServletRequest req) {
        curriculumService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok(null, "Curriculum deleted", req.getRequestURI()));
    }
}

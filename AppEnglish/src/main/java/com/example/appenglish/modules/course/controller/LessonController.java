package com.example.appenglish.modules.course.controller;

import com.example.appenglish.modules.course.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {
    private final LessonService lessonService;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getLessonDetails(@PathVariable Long id) {
        return ResponseEntity.ok(lessonService.getLessonDetails(id));
    }
}
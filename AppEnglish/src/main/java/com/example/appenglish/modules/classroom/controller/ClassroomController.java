package com.example.appenglish.modules.classroom.controller;

import com.example.appenglish.modules.classroom.dto.ClassroomResponse;
import com.example.appenglish.modules.classroom.service.ClassroomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/classrooms")
@RequiredArgsConstructor
@Tag(name = "Classroom")
public class ClassroomController {

    private final ClassroomService classroomService;

    @GetMapping("/{id}")
    @Operation(summary = "Get class details and overall completion progress")
    public ResponseEntity<ClassroomResponse> getClassDetails(@PathVariable Long id) {
        return ResponseEntity.ok(classroomService.getClassDetails(id));
    }

    @GetMapping("/{id}/progress")
    @Operation(summary = "Get specific learning progress for a student in a class")
    public ResponseEntity<Double> getLearningProgress(
            @PathVariable Long id,
            @RequestParam Long userId) {
        return ResponseEntity.ok(classroomService.calculateLearningProgress(userId, id));
    }

    @PutMapping("/{classId}/students/{studentId}/complete")
    @Operation(summary = "Update student completion status for the entire class")
    public ResponseEntity<Void> updateStudentCompletion(
            @PathVariable Long classId,
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "true") boolean completed) {
        classroomService.updateStudentCompletion(classId, studentId, completed);
        return ResponseEntity.noContent().build();
    }
}
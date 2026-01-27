package com.example.appenglish.modules.classroom.controller;

import com.example.appenglish.modules.classroom.dto.ClassroomResponse;
import com.example.appenglish.modules.classroom.service.ClassroomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller cho Classroom API.
 */
@RestController
@RequestMapping("/api/classrooms")
@RequiredArgsConstructor
@Tag(name = "Classroom")
public class ClassroomController {

    private final ClassroomService classroomService;

    /**
     * Lấy chi tiết lớp học với tính toán % tiến độ.
     */
    @GetMapping("/{id}")
    @Operation()
    public ResponseEntity<ClassroomResponse> getClassDetails(@PathVariable Long id) {
        ClassroomResponse response = classroomService.getClassDetails(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Cập nhật trạng thái hoàn thành cho học viên trong lớp.
     */
    @PutMapping("/{classId}/students/{studentId}/complete")
    @Operation()
    public ResponseEntity<Void> updateStudentCompletion(
            @PathVariable Long classId,
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "true") boolean completed) {
        classroomService.updateStudentCompletion(classId, studentId, completed);
        return ResponseEntity.noContent().build();
    }
}

package com.example.appenglish.modules.classroom.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.enums.ClassStatus;
import com.example.appenglish.modules.classroom.dto.ClassroomDto;
import com.example.appenglish.modules.classroom.dto.ClassroomRequest;
import com.example.appenglish.modules.classroom.service.ClassroomService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/classrooms")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classroomService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<ClassroomDto>>> getAllClassrooms(HttpServletRequest httpReq) {
        List<ClassroomDto> classrooms = classroomService.getAllClassrooms();
        return ResponseEntity.ok(
                BaseResponse.ok(classrooms, "Get all classrooms successfully", httpReq.getRequestURI())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ClassroomDto>> getClassroomById(
            @PathVariable Long id,
            HttpServletRequest httpReq) {
        ClassroomDto classroom = classroomService.getClassroomById(id);
        return ResponseEntity.ok(
                BaseResponse.ok(classroom, "Get classroom successfully", httpReq.getRequestURI())
        );
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<BaseResponse<List<ClassroomDto>>> getClassroomsByTeacher(
            @PathVariable String teacherId,
            HttpServletRequest httpReq) {
        List<ClassroomDto> classrooms = classroomService.getClassroomsByTeacher(teacherId);
        return ResponseEntity.ok(
                BaseResponse.ok(classrooms, "Get classrooms by teacher successfully", httpReq.getRequestURI())
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<BaseResponse<List<ClassroomDto>>> getClassroomsByStatus(
            @PathVariable ClassStatus status,
            HttpServletRequest httpReq) {
        List<ClassroomDto> classrooms = classroomService.getClassroomsByStatus(status);
        return ResponseEntity.ok(
                BaseResponse.ok(classrooms, "Get classrooms by status successfully", httpReq.getRequestURI())
        );
    }

    @PostMapping
    public ResponseEntity<BaseResponse<ClassroomDto>> createClassroom(
            @Valid @RequestBody ClassroomRequest request,
            HttpServletRequest httpReq) {
        ClassroomDto classroom = classroomService.createClassroom(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.ok(classroom, "Classroom created successfully", httpReq.getRequestURI()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<ClassroomDto>> updateClassroom(
            @PathVariable Long id,
            @Valid @RequestBody ClassroomRequest request,
            HttpServletRequest httpReq) {
        ClassroomDto classroom = classroomService.updateClassroom(id, request);
        return ResponseEntity.ok(
                BaseResponse.ok(classroom, "Classroom updated successfully", httpReq.getRequestURI())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<?>> deleteClassroom(
            @PathVariable Long id,
            HttpServletRequest httpReq) {
        classroomService.deleteClassroom(id);
        return ResponseEntity.ok(
                BaseResponse.ok(null, "Classroom deleted successfully", httpReq.getRequestURI())
        );
    }
}

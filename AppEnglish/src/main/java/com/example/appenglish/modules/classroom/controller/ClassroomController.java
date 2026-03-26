package com.example.appenglish.modules.classroom.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.modules.classroom.dto.ClassroomUpsertRequest;
import com.example.appenglish.modules.classroom.service.ClassroomService;
import com.example.appenglish.modules.teacher.dto.ClassDto;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/classrooms")
@RequiredArgsConstructor
public class ClassroomController {

  private final ClassroomService classroomService;

  @GetMapping
  public ResponseEntity<BaseResponse<List<ClassDto>>> getAll(HttpServletRequest req) {
    return ResponseEntity.ok(BaseResponse.ok(classroomService.getAll(), "OK", req.getRequestURI()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<BaseResponse<ClassDto>> getById(@PathVariable Long id, HttpServletRequest req) {
    return ResponseEntity.ok(BaseResponse.ok(classroomService.getById(id), "OK", req.getRequestURI()));
  }

  @GetMapping("/teacher/{teacherId}")
  public ResponseEntity<BaseResponse<List<ClassDto>>> getByTeacher(
      @PathVariable String teacherId, HttpServletRequest req) {
    return ResponseEntity.ok(
        BaseResponse.ok(classroomService.getByTeacher(teacherId), "OK", req.getRequestURI()));
  }

  @PostMapping
  public ResponseEntity<BaseResponse<ClassDto>> create(
      @RequestBody ClassroomUpsertRequest body, HttpServletRequest req) {
    return ResponseEntity.ok(BaseResponse.ok(classroomService.create(body), "Created", req.getRequestURI()));
  }

  @PutMapping("/{id}")
  public ResponseEntity<BaseResponse<ClassDto>> update(
      @PathVariable Long id, @RequestBody ClassroomUpsertRequest body, HttpServletRequest req) {
    return ResponseEntity.ok(BaseResponse.ok(classroomService.update(id, body), "Updated", req.getRequestURI()));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id, HttpServletRequest req) {
    classroomService.delete(id);
    return ResponseEntity.ok(BaseResponse.ok(null, "Deleted", req.getRequestURI()));
  }
}


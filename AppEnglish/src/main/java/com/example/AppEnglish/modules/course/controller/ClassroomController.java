package com.example.AppEnglish.modules.course.controller;

import com.example.AppEnglish.common.dto.BaseResponse;
import com.example.AppEnglish.entity.User;
import com.example.AppEnglish.modules.course.dto.ClassDetailDTO;
import com.example.AppEnglish.modules.course.service.ClassroomService;
import com.example.AppEnglish.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/classes")
@RequiredArgsConstructor
@Tag(name = "Classroom", description = "Classroom management APIs")
public class ClassroomController {

    private final ClassroomService classroomService;
    private final UserRepository userRepository;

    @GetMapping("/{classId}")
    @Operation(summary = "Get class details", description = "Get detailed information of a classroom including progress")
    public ResponseEntity<BaseResponse<ClassDetailDTO>> getClassDetails(
            @PathVariable Long classId,
            Authentication authentication,
            HttpServletRequest request) {

        Long currentUserId = getCurrentUserId(authentication);
        ClassDetailDTO classDetail = classroomService.getClassDetails(classId, currentUserId);

        return ResponseEntity.ok(BaseResponse.ok(classDetail, "Class details retrieved", request.getRequestURI()));
    }

    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return null;
        }
        return userRepository.findByEmail(authentication.getName())
                .map(User::getId)
                .orElse(null);
    }
}

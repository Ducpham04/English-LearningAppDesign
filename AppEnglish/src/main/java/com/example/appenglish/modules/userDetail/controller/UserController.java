package com.example.appenglish.modules.userDetail.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.modules.userDetail.dto.UserDataDto;
import com.example.appenglish.modules.userDetail.dto.UserProfileRequest;
import com.example.appenglish.modules.userDetail.dto.UserProfileResponse;
import com.example.appenglish.modules.userDetail.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ========================= GET CURRENT USER =========================
    @GetMapping("/me")
    public ResponseEntity<BaseResponse<UserDataDto>> getCurrentUser(HttpServletRequest httpReq) {
        try {
            UserDataDto user = userService.getCurrentUser();

            return ResponseEntity.ok(
                    BaseResponse.ok(
                            user,
                            "Get current user successfully",
                            httpReq.getRequestURI()
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(
                            BaseResponse.error(
                                    "User not authenticated",
                                    "UNAUTHORIZED",
                                    httpReq.getRequestURI()
                            )
                    );
        }
    }

    // ========================= UPDATE PROFILE =========================
    @PatchMapping("/me")
    public ResponseEntity<BaseResponse<UserProfileResponse>> updateProfile(
            @Valid @RequestBody UserProfileRequest req,
            HttpServletRequest httpReq) {

        try {
            UserProfileResponse updatedProfile = userService.updateProfile(req);

            return ResponseEntity.ok(
                    BaseResponse.ok(
                            updatedProfile,
                            "Profile updated successfully",
                            httpReq.getRequestURI()
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(
                            BaseResponse.error(
                                    "User not authenticated",
                                    "UNAUTHORIZED",
                                    httpReq.getRequestURI()
                            )
                    );
        }
    }
}

package com.example.appenglish.modules.auth.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.modules.auth.dto.*;
import com.example.appenglish.modules.auth.service.AuthService;
import com.example.appenglish.service.UserDetailsServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private final AuthService authService;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @PostMapping("/login")
    @Operation(summary = "User Login")
    public ResponseEntity<BaseResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest req,
            HttpServletRequest httpReq) {

        LoginResponse data = authService.login(req.getEmail(), req.getPassword());
        return ResponseEntity.ok(BaseResponse.ok(data, "Login successful", httpReq.getRequestURI()));
    }

    @PostMapping("/register")
    @Operation(summary = "User Registration")
    public ResponseEntity<BaseResponse<String>> register(
            @Valid @RequestBody RegisterRequest req,
            HttpServletRequest httpReq) {

        boolean isSuccess = userDetailsServiceImpl.register(req);
        if (!isSuccess) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(BaseResponse.error("Email already exists", "DUPLICATE_EMAIL", httpReq.getRequestURI()));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.ok(null, "Registration successful", httpReq.getRequestURI()));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh Token")
    public ResponseEntity<BaseResponse<TokenResponseDto>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest req,
            HttpServletRequest httpReq) {

        TokenResponseDto data = authService.refreshToken(req.getRefreshToken());
        return ResponseEntity.ok(BaseResponse.ok(data, "Token refreshed", httpReq.getRequestURI()));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout")
    public ResponseEntity<BaseResponse<Void>> logout(HttpServletRequest httpReq) {
        return ResponseEntity.ok(BaseResponse.ok(null, "Logout successful", httpReq.getRequestURI()));
    }

    @GetMapping("/users")
    @Operation(summary = "Get All Users")
    public ResponseEntity<BaseResponse<List<?>>> getAllUsers(HttpServletRequest httpReq) {
        List<?> users = userDetailsServiceImpl.getUsers();
        return ResponseEntity.ok(BaseResponse.ok(users, "Fetch users successful", httpReq.getRequestURI()));
    }
}
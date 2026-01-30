package com.example.appenglish.modules.auth.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.modules.auth.dto.*;
import com.example.appenglish.modules.auth.service.AuthService;
import com.example.appenglish.security.JwtUtils;
import com.example.appenglish.service.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    // ========================= LOGIN =========================
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest req,
            HttpServletRequest httpReq) {

        try {
            LoginResponse data = authService.login(req.getEmail(), req.getPassword());

            return ResponseEntity.ok(
                    BaseResponse.ok(
                            data,
                            "Login successful",
                            httpReq.getRequestURI()
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(
                            BaseResponse.error(
                                    "Invalid email or password",
                                    "INVALID_CREDENTIALS",
                                    httpReq.getRequestURI()
                            )
                    );
        }
    }

    // ========================= REFRESH TOKEN =========================
    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<TokenResponseDto>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest req,
            HttpServletRequest httpReq) {

        try {
            TokenResponseDto data = authService.refreshToken(req.getRefreshToken());

            return ResponseEntity.ok(
                    BaseResponse.ok(
                            data,
                            "Token refreshed successfully",
                            httpReq.getRequestURI()
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(
                            BaseResponse.error(
                                    "Invalid refresh token",
                                    "INVALID_REFRESH_TOKEN",
                                    httpReq.getRequestURI()
                            )
                    );
        }
    }

    // ========================= LOGOUT =========================
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<?>> logout(
            HttpServletRequest httpReq) {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                authService.logout(authentication.getName());
            }

            return ResponseEntity.ok(
                    BaseResponse.ok(
                            null,
                            "Logout successful",
                            httpReq.getRequestURI()
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            BaseResponse.error(
                                    "Logout failed",
                                    "LOGOUT_FAILED",
                                    httpReq.getRequestURI()
                            )
                    );
        }
    }

    // ========================= RESET PASSWORD =========================
    @PostMapping("/reset-password")
    public ResponseEntity<BaseResponse<?>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest req,
            HttpServletRequest httpReq) {

        try {
            authService.resetPassword(req.getEmail());

            return ResponseEntity.ok(
                    BaseResponse.ok(
                            null,
                            "Password reset email sent",
                            httpReq.getRequestURI()
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(
                            BaseResponse.error(
                                    "User not found",
                                    "USER_NOT_FOUND",
                                    httpReq.getRequestURI()
                            )
                    );
        }
    }

    // ========================= REGISTER =========================
    @PostMapping("/register/local")
    public ResponseEntity<BaseResponse<String>> registerLocal(
            @Valid @RequestBody RegisterRequest req,
            HttpServletRequest httpReq) {

        boolean isSuccess = userDetailsServiceImpl.register(req);

        if (!isSuccess) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(
                            BaseResponse.error(
                                    "Email đã tồn tại, vui lòng dùng email khác!",
                                    "DUPLICATE_EMAIL",
                                    httpReq.getRequestURI()
                            )
                    );
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        BaseResponse.ok(
                                null,
                                "Đăng ký thành công",
                                httpReq.getRequestURI()
                        )
                );
    }

    // ========================= GET ALL USERS =========================
    @GetMapping("/users")
    public ResponseEntity<BaseResponse<List<?>>> getAllUsers(HttpServletRequest httpReq) {

        List<?> users = userDetailsServiceImpl.getUsers();

        return ResponseEntity.ok(
                BaseResponse.ok(
                        users,
                        "Get users successfully",
                        httpReq.getRequestURI()
                )
        );
    }
}

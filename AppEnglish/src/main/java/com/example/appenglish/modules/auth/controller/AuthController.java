package com.example.appenglish.modules.auth.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.modules.auth.dto.LoginRequest;
import com.example.appenglish.modules.auth.dto.LoginResponse;
import com.example.appenglish.modules.auth.dto.RegisterRequest;
import com.example.appenglish.security.JwtUtils;
import com.example.appenglish.service.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    // ========================= LOGIN =========================
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest req,
            HttpServletRequest httpReq) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            req.getEmail(),
                            req.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtUtils.generateToken(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            Set<String> authorities = authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            LoginResponse data = new LoginResponse(
                    "Bearer",
                    token,
                    userDetails.getUsername(),
                    authorities
            );

            return ResponseEntity.ok(
                    BaseResponse.ok(
                            data,
                            "Login successful",
                            httpReq.getRequestURI()
                    )
            );

        } catch (org.springframework.security.authentication.BadCredentialsException ex) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(
                            BaseResponse.error(
                                    "Email hoặc mật khẩu không đúng",
                                    "INVALID_CREDENTIALS",
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
        // TODO: Thêm kiểm tra quyền admin sau khi test xong
        /*
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(
                            BaseResponse.error(
                                    "Bạn không có quyền truy cập danh sách người dùng",
                                    "FORBIDDEN",
                                    httpReq.getRequestURI()
                            )
                    );
        }
        */

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

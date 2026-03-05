package com.example.AppEnglish.modules.auth.service;

import com.example.AppEnglish.entity.User;
import com.example.AppEnglish.modules.auth.dto.LoginResponse;
import com.example.AppEnglish.modules.auth.dto.TokenResponseDto;
import com.example.AppEnglish.modules.userDetail.dto.UserDataDto;
import com.example.AppEnglish.repository.UserRepository;
import com.example.AppEnglish.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final UserConverterService userConverterService;
    private final UserDetailsService userDetailsService;

    public LoginResponse login(String email, String password) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get user from database
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate tokens
        String accessToken = jwtUtils.generateToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(email);

        // Convert user to UserDataDto
        UserDataDto userDataDto = userConverterService.convertUserToDataDto(user);

        return LoginResponse.builder()
                .user(userDataDto)
                .token(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public TokenResponseDto refreshToken(String refreshToken) {
        // Validate refresh token and ensure it's actually a refresh token
        if (!jwtUtils.validate(refreshToken) || !jwtUtils.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // Get email from token
        String email = jwtUtils.getSubject(refreshToken);

        // Ensure user exists
        userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Load full user details (with authorities) for new access token
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        // Generate new tokens
        String newAccessToken = jwtUtils.generateToken(authentication);
        String newRefreshToken = jwtUtils.generateRefreshToken(email);

        return TokenResponseDto.builder()
                .token(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    public void logout(String email) {
        // Invalidate session/token on server side if needed
        // For JWT, logout is typically handled on client side
        SecurityContextHolder.clearContext();
        log.info("User logged out: {}", email);
    }

    public void resetPassword(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // TODO: Send password reset email
        log.info("Password reset requested for: {}", email);
    }
}

package com.example.appenglish.config;

import com.example.appenglish.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity // Kích hoạt tính năng bảo mật web
@EnableMethodSecurity // Cho phép dùng @PreAuthorize ở Controller
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Tắt CSRF vì chúng ta dùng Token (Stateless), không dùng Cookie/Session
                .csrf(csrf -> csrf.disable())
<<<<<<< HEAD
                
                // 2. Cấu hình CORS để các ứng dụng khác (React, Vue, Mobile) gọi được API
                .cors(Customizer.withDefaults()) 
=======
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Auth
                        .requestMatchers("/v1/auth/**",
                                    "/v1/auth/register/**",
                                "/v1/users/**").permitAll()
>>>>>>> origin/develop

                // 3. Cấu hình Stateless (Không lưu trạng thái người dùng trên server)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // 4. Phân quyền đường dẫn
                .authorizeHttpRequests(auth -> auth
                        // Cho phép truy cập công khai các API Auth (Login, Register)
                        .requestMatchers("/api/auth/**").permitAll()
                        
                        // Cho phép truy cập Swagger để xem tài liệu API
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/webjars/**"
                        ).permitAll()

                        // Các API còn lại bắt buộc phải có Token hợp lệ
                        .anyRequest().authenticated()
                );

        // 5. Thêm Filter kiểm tra JWT trước khi đến bước xác thực User mặc định
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

<<<<<<< HEAD
    // Cấu hình CORS: Cho phép Frontend truy cập vào API từ các Domain khác nhau
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*")); // Trong thực tế nên thay "*" bằng domain của Frontend
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
=======



}

>>>>>>> origin/develop

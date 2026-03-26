package com.example.appenglish.config;

import com.example.appenglish.security.JwtAuthFilter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import org.springframework.util.StringUtils;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final Environment environment;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // TẠM THỜI: mở full cho dev (Swagger + FE test)
                        .anyRequest().permitAll()
                );

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Dùng Spring Environment (application.properties + env) — không chỉ System.getenv()
        // vì mvn/IDE thường không load file .env của Docker Compose.
        // Pattern localhost:* / 127.0.0.1:* để mọi port dev (5173, 3000, …) đều qua CORS khi dùng credentials.
        String raw = environment.getProperty("app.cors.allowed-origins");
        if (!StringUtils.hasText(raw)) {
            raw =
                    "http://localhost:5173,http://localhost:3000,http://127.0.0.1:5173,http://127.0.0.1:3000";
        }
        Set<String> patterns = new LinkedHashSet<>();
        patterns.add("http://localhost:*");
        patterns.add("http://127.0.0.1:*");
        for (String part : raw.split(",")) {
            String t = part.trim();
            if (StringUtils.hasText(t)) {
                patterns.add(t);
            }
        }
        configuration.setAllowedOriginPatterns(new ArrayList<>(patterns));

        // Cho phép các method HTTP cần thiết
        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        // Cho phép headers (bao gồm Authorization)
        configuration.setAllowedHeaders(List.of("*"));

        // Cho phép gửi credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);

        // Preflight request cache time (30 phút)
        configuration.setMaxAge(1800L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}


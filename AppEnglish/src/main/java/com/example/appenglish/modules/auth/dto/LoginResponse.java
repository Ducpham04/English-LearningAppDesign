package com.example.appenglish.modules.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String tokenType;
    private String accessToken;
    private String email;
    private Set<String> authorities;
}


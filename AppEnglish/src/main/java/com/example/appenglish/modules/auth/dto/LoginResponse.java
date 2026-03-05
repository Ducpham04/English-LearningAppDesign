package com.example.AppEnglish.modules.auth.dto;

import com.example.AppEnglish.modules.userDetail.dto.UserDataDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private UserDataDto user;
    private String token;
    private String refreshToken;
}


package com.example.appenglish.modules.auth.dto;

import com.example.appenglish.modules.userDetail.dto.UserDataDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {
    private UserDataDto user;
    private String token;
    private String refreshToken;
}

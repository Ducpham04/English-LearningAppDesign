package com.example.appenglish.modules.userDetail.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileRequest {
    private String name;
    private String avatar;
    private SettingsDto settings;
}

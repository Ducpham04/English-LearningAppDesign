package com.example.AppEnglish.modules.userDetail.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettingsDto {
    private Boolean notifications;
    private Boolean darkMode;
    private String language;
}

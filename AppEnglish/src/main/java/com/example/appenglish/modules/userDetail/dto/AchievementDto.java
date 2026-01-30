package com.example.appenglish.modules.userDetail.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AchievementDto {
    private String id;
    private String title;
    private Integer progress;
    private Integer target;
    private Boolean completed;
}

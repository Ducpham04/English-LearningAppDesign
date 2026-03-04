package com.example.appenglish.modules.student.dto;

import lombok.*;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeDto {
    private Long id;
    private String name;
    private String iconUrl;
    private Map<String, Object> conditionRule;
    private String earnedAt;
    private Boolean isEarned;
}

package com.example.appenglish.modules.student.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {
    private Integer totalXP;
    private Integer currentLevel;
    private Integer xpToNextLevel;
    private Integer streak;
    private Integer lessonsCompleted;
    private Integer wordsLearned;
    private Integer testsCompleted;
    private Integer badgesEarned;
}

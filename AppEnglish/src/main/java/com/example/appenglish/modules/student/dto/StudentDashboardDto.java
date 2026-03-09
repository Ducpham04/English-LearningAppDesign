package com.example.appenglish.modules.student.dto;

import com.example.appenglish.modules.curriculum.dto.CurriculumDto;
import com.example.appenglish.modules.userDetail.dto.UserDataDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StudentDashboardDto {
    private UserDataDto user;
    private List<CurriculumDto> courses;
    private DashboardStatsDto stats;

    @Data
    @Builder
    public static class DashboardStatsDto {
        private Integer totalXP;
        private Integer currentLevel;
        private Integer xpToNextLevel;
        private Integer streak;
        private Integer lessonsCompleted;
        private Integer wordsLearned;
        private Integer testsCompleted;
        private Integer badgesEarned;
    }
}

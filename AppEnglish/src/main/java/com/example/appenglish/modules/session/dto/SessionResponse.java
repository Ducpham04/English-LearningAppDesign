package com.example.appenglish.modules.session.dto;

import com.example.appenglish.modules.vocabulary.dto.VocabularyResponse;
import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponse {
    private Long id;
    private String title;
    private String description;
    private String level;
    private String thumbnail;
    private Integer totalLessons;
    private Long completedLessons;
    private Integer estimatedTime;
    private String status;
    private Integer order;
    private String unlockCondition;
    
    private List<SessionLessonResponse> lessons;
    private Integer vocabularyCount;
    private UserProgressData userProgress;

    // QUAN TRỌNG: Phải là public static class
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionLessonResponse {
        private Long id;
        private String title;
        private String type;
        private String description;
        private Integer duration;
        private String status;
        private Integer order;
        private Double score;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserProgressData {
        private Double completion;
        private Double averageScore;
        private Integer timeSpent;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionListWrapper {
        private List<SessionResponse> sessions;
        private VocabularyResponse.PaginationData pagination;
    }
}
package com.example.AppEnglish.modules.course.dto;

import com.example.AppEnglish.entity.Session;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionListDTO {
    private Long id;
    private String title;
    private String description;
    private String level;
    private String thumbnail;
    private int totalLessons;
    private int completedLessons;
    private Integer estimatedTime;
    private String status;
    private Integer order;
    private String unlockCondition;

    public static SessionListDTO fromEntity(Session session) {
        int totalLessons = session.getLessons() != null ? session.getLessons().size() : 0;
        int completedLessons = 0;

        String sessionStatus = "locked";
        if (completedLessons == totalLessons && totalLessons > 0) {
            sessionStatus = "completed";
        } else if (completedLessons > 0) {
            sessionStatus = "in-progress";
        }

        return SessionListDTO.builder()
                .id(session.getId())
                .title(session.getTitle())
                .description(session.getDescription())
                .level(session.getLevel())
                .thumbnail(session.getThumbnailUrl())
                .totalLessons(totalLessons)
                .completedLessons(completedLessons)
                .estimatedTime(session.getEstimatedTime())
                .status(sessionStatus)
                .order(session.getOrderIndex())
                .unlockCondition(null)
                .build();
    }
}

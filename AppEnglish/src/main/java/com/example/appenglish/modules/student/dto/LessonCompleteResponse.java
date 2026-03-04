package com.example.appenglish.modules.student.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonCompleteResponse {
    private int xpEarned;
    private int level;
    private int xp;
    private int totalLessonsCompleted;
}

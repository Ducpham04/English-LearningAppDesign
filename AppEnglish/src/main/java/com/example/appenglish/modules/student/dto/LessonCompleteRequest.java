package com.example.appenglish.modules.student.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonCompleteRequest {
    /** Score 0-100, optional. Used to compute bonus XP. */
    private Integer score;
}

package com.example.appenglish.modules.test.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResultItem {
    private Long questionId;
    private Boolean isCorrect;
    private Integer scoreEarned;
    private Integer points;
}

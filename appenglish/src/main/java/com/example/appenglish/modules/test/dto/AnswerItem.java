package com.example.appenglish.modules.test.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerItem {
    private Long questionId;
    /** Với MCQ: id của option (số). Với FILL_BLANK/SPEAKING: chuỗi text. */
    private String userAnswer;
}

package com.example.appenglish.modules.test.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitAttemptRequest {
    /** questionId -> userAnswer (optionId as string, hoặc text với FILL_BLANK/SPEAKING) */
    private List<AnswerItem> answers;
}

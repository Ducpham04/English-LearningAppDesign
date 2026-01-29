package com.example.appenglish.modules.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Map;

@Data
public class QuizSubmitRequest {
    @Schema(description = "Danh sách đáp án: Key là QuestionID, Value là OptionID", 
            example = "{ \"1\": 10, \"2\": 15 }")
    private Map<Long, Long> answers;
}
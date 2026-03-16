package com.example.appenglish.modules.test.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionTakeDto {
    private Long id;
    private String content;
    private String type; // MCQ, FILL_BLANK, SPEAKING
    private Integer points;
    private Integer orderIndex;
    /** Chỉ có với MCQ: danh sách option (id + content), không có isCorrect. */
    private List<OptionTakeDto> options;
}

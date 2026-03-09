package com.example.appenglish.modules.test.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestOptionCreateRequest {
    private String content;
    private Boolean isCorrect;
}

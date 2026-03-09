package com.example.appenglish.modules.session.dto;

import lombok.Data;

@Data
public class SessionCreateRequest {
    private String title;
    private Long courseId;
    private Integer orderIndex;
}

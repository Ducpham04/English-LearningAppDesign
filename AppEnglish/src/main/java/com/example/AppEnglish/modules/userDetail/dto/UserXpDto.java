package com.example.appenglish.modules.userDetail.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserXpDto {
    private Integer rank; // Thứ tự xếp hạng
    private Long userId;
    private String displayName;
    private Integer totalXp;
}

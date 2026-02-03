package com.example.appenglish.modules.userDetail.dto;
import lombok.Data;

@Data
public class XPRequest {
    private Long userId;      // ID của user
    private Integer amount;   // Số XP cần cộng
    private String actionType; // Nguồn XP (VD: TestCompletion, DailyMission)
}

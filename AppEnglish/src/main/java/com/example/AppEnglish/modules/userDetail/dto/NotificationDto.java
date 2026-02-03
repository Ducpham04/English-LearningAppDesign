package com.example.appenglish.modules.userDetail.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
public class NotificationDto {
    @Schema(description = "ID của user nhận thông báo", example = "5")
    private Long userId;               // ID của user
    @Schema(description = "Nội dung thông báo", example = "Chào e nha")
    private String message;            // Nội dung thông báo
    @Schema(description = "Metadata bổ sung cho thông báo (Type,Source,Time")
    private Map<String, Object> metadata; // Thông tin JSON metadata bổ sung
}

package com.example.appenglish.modules.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thông tin phản hồi sau khi hoàn thành bài học")
public class LessonCompleteResponse {

    @Schema(description = "ID của bài học vừa hoàn thành", example = "lsn_001")
    private String lessonId;

    @Schema(description = "Trạng thái hoàn thành bài học", example = "true")
    private boolean completed;

    @Schema(description = "Điểm số cuối cùng đạt được", example = "85.0")
    private Double score;

    @Schema(description = "Số điểm kinh nghiệm (XP) nhận được", example = "50")
    private Integer xpEarned;

    @Schema(description = "Danh sách mã huy hiệu mới mở khóa (nếu có)", example = "[\"badge_first_lesson\"]")
    private List<String> badgesUnlocked;

    @Schema(description = "Thông tin bài học tiếp theo trong lộ trình")
    private NextLessonInfo nextLesson;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Chi tiết về bài học tiếp theo")
    public static class NextLessonInfo {
        
        @Schema(description = "ID bài học tiếp theo", example = "lsn_002")
        private String id;

        @Schema(description = "Tiêu đề bài học tiếp theo", example = "Pronunciation Practice")
        private String title;

        @Schema(description = "Trạng thái của bài học tiếp theo", example = "locked")
        private String status;
    }
}
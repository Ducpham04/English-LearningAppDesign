package com.example.appenglish.modules.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonCompleteRequest {

    @Schema(description = "Điểm số đạt được (tự động tính nếu gửi kèm answers)", example = "85")
    private Double score;

    @Schema(description = "Thời gian học thực tế tính bằng phút", example = "5")
    private Integer timeSpent;

    @Schema(
        description = "Bản đồ đáp án bài Quiz (ID câu hỏi -> ID lựa chọn). Nếu có trường này, hệ thống sẽ ưu tiên chấm điểm dựa trên đáp án.", 
        example = "{\"1\": 101, \"2\": 105}"
    )
    private Map<Long, Long> answers;

    @Schema(description = "Dữ liệu bổ sung dành cho bài học Video hoặc Vocabulary")
    private ExtraData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Thông tin chi tiết về quá trình tương tác trong bài học")
    public static class ExtraData {
        
        @Schema(description = "Trạng thái đã xem hết video bài học hay chưa", example = "true")
        private boolean watchedVideo;

        @Schema(
            description = "Danh sách các từ vựng người dùng đã đánh dấu/lưu lại trong bài học", 
            example = "[\"hello\", \"introduction\", \"greeting\"]"
        )
        private List<String> markedWords;
    }
}
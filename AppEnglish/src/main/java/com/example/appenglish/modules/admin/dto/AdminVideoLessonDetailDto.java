package com.example.appenglish.modules.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Chi tiết bài học có video: dùng cho báo cáo quản lý nội dung.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminVideoLessonDetailDto {
    private Long lessonId;
    private String lessonTitle;
    private String lessonType;
    private Integer lessonOrderIndex;

    private Long sessionId;
    private String sessionTitle;

    private Long topicId;
    private String topicName;

    private String videoUrl;
    private String youtubeUrl;
    /** Số segment trong transcript (số câu/đoạn). */
    private Integer transcriptSegmentCount;
    private boolean hasTranscript;
}

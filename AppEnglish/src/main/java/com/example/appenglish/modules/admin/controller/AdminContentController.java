package com.example.appenglish.modules.admin.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.modules.admin.dto.*;
import com.example.appenglish.modules.admin.service.AdminContentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API quản lý nội dung bài học: báo cáo video lessons, bản ghi phát âm, chấm phát âm & phân tích AI.
 * Dữ liệu thật từ DB (lesson_videos, pronunciation_attempts).
 */
@RestController
@RequestMapping("/v1/admin/content")
@RequiredArgsConstructor
public class AdminContentController {

    private final AdminContentService adminContentService;

    /**
     * Báo cáo tổng quan: số bài học, bài có video, tổng bản ghi phát âm, số người dùng có thu âm.
     */
    @GetMapping("/report")
    public ResponseEntity<BaseResponse<AdminContentReportDto>> getReport(HttpServletRequest req) {
        AdminContentReportDto data = adminContentService.getReport();
        return ResponseEntity.ok(BaseResponse.ok(data, "OK", req.getRequestURI()));
    }

    /**
     * Danh sách chi tiết các bài học có video: lesson, session, topic, video URL, transcript (số segment).
     */
    @GetMapping("/video-lessons")
    public ResponseEntity<BaseResponse<List<AdminVideoLessonDetailDto>>> getVideoLessonsDetail(HttpServletRequest req) {
        List<AdminVideoLessonDetailDto> data = adminContentService.getVideoLessonsDetail();
        return ResponseEntity.ok(BaseResponse.ok(data, "OK", req.getRequestURI()));
    }

    /**
     * Bản ghi phát âm (có phân trang). Lọc theo lessonId hoặc userId nếu cần.
     * Trả về chi tiết chấm điểm (pronunciation, accuracy, fluency, prosody, completeness) và feedback AI.
     */
    @GetMapping("/pronunciation-records")
    public ResponseEntity<BaseResponse<Page<AdminPronunciationRecordDto>>> getPronunciationRecords(
            @RequestParam(required = false) Long lessonId,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest req
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AdminPronunciationRecordDto> data = adminContentService.getPronunciationRecords(lessonId, userId, pageable);
        return ResponseEntity.ok(BaseResponse.ok(data, "OK", req.getRequestURI()));
    }

    /**
     * Tổng hợp theo người dùng: số lần thu âm, điểm trung bình (chấm phát âm), lần thu âm gần nhất.
     */
    @GetMapping("/pronunciation-users")
    public ResponseEntity<BaseResponse<List<AdminPronunciationUserSummaryDto>>> getPronunciationUserSummaries(HttpServletRequest req) {
        List<AdminPronunciationUserSummaryDto> data = adminContentService.getPronunciationUserSummaries();
        return ResponseEntity.ok(BaseResponse.ok(data, "OK", req.getRequestURI()));
    }
}

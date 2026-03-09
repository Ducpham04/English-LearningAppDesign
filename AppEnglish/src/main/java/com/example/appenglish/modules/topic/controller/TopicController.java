package com.example.appenglish.modules.topic.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.modules.session.dto.SessionCreateRequest;
import com.example.appenglish.modules.topic.dto.TopicCreateRequest;
import com.example.appenglish.modules.topic.dto.TopicDto;
import com.example.appenglish.modules.topic.service.TopicService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    /** Student: lấy danh sách topic đang hoạt động */
    @GetMapping
    public ResponseEntity<BaseResponse<List<TopicDto>>> getActive(HttpServletRequest req) {
        return ResponseEntity.ok(BaseResponse.ok(topicService.getActive(), "OK", req.getRequestURI()));
    }

    /** Admin: lấy tất cả topic */
    @GetMapping("/all")
    public ResponseEntity<BaseResponse<List<TopicDto>>> getAll(HttpServletRequest req) {
        return ResponseEntity.ok(BaseResponse.ok(topicService.getAll(), "OK", req.getRequestURI()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<TopicDto>> getById(@PathVariable Long id, HttpServletRequest req) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(topicService.getById(id), "OK", req.getRequestURI()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(BaseResponse.error(e.getMessage(), "NOT_FOUND", req.getRequestURI()));
        }
    }

    /** Admin: tạo topic mới */
    @PostMapping
    public ResponseEntity<BaseResponse<TopicDto>> create(@RequestBody TopicCreateRequest req,
                                                         HttpServletRequest httpReq) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(topicService.create(req), "Topic created", httpReq.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(BaseResponse.error(e.getMessage(), "BAD_REQUEST", httpReq.getRequestURI()));
        }
    }

    /** Admin: cập nhật topic */
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<TopicDto>> update(@PathVariable Long id,
                                                         @RequestBody TopicCreateRequest req,
                                                         HttpServletRequest httpReq) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(topicService.update(id, req), "Topic updated", httpReq.getRequestURI()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(BaseResponse.error(e.getMessage(), "NOT_FOUND", httpReq.getRequestURI()));
        }
    }

    /** Admin: bật/tắt topic */
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<BaseResponse<Void>> toggle(@PathVariable Long id, HttpServletRequest req) {
        topicService.toggleActive(id);
        return ResponseEntity.ok(BaseResponse.ok(null, "Toggled", req.getRequestURI()));
    }

    /** Admin: xóa topic */
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id, HttpServletRequest req) {
        topicService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok(null, "Topic deleted", req.getRequestURI()));
    }

    /** Admin: thêm session vào topic */
    @PostMapping("/{topicId}/sessions")
    public ResponseEntity<BaseResponse<TopicDto.TopicSessionDto>> addSession(
            @PathVariable Long topicId,
            @RequestBody SessionCreateRequest req,
            HttpServletRequest httpReq) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(topicService.addSession(topicId, req), "Session added", httpReq.getRequestURI()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(BaseResponse.error(e.getMessage(), "BAD_REQUEST", httpReq.getRequestURI()));
        }
    }
}

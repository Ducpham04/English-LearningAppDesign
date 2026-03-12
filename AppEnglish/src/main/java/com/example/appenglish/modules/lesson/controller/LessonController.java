package com.example.appenglish.modules.lesson.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.modules.lesson.dto.LessonCreateRequest;
import com.example.appenglish.modules.lesson.dto.LessonDto;
import com.example.appenglish.modules.lesson.dto.LessonVideoRequest;
import com.example.appenglish.modules.lesson.service.LessonService;
import com.example.appenglish.modules.transcribe.service.s3.StorageService;
import com.example.appenglish.repository.LessonVideoRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;
    private final LessonVideoRepository lessonVideoRepository;
    private final StorageService storageService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<LessonDto>>> getAll(HttpServletRequest req) {
        return ResponseEntity.ok(BaseResponse.ok(lessonService.getAll(), "OK", req.getRequestURI()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<LessonDto>> getById(@PathVariable Long id, HttpServletRequest req) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(lessonService.getById(id), "OK", req.getRequestURI()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(BaseResponse.error(e.getMessage(), "NOT_FOUND", req.getRequestURI()));
        }
    }

    /**
     * Stream video qua backend để tránh CORS khi phát trong web (same-origin với API).
     * Hỗ trợ Range request để seek/tua video.
     */
    @GetMapping(value = "/{id}/video-stream", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void streamVideo(@PathVariable Long id,
                            HttpServletRequest request,
                            HttpServletResponse response) throws IOException {
        var lv = lessonVideoRepository.findById(id).orElse(null);
        if (lv == null || lv.getVideoUrl() == null || lv.getVideoUrl().isBlank()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Lesson video not found");
            return;
        }
        String key = storageService.getVideoKey(lv.getVideoUrl());
        if (key == null || key.isBlank()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Video key not found");
            return;
        }
        String rangeHeader = request.getHeader("Range");
        try (var streamResult = storageService.openVideoStream(key, rangeHeader)) {
            response.setContentType(streamResult.getContentType());
            response.setHeader("Accept-Ranges", "bytes");
            if (streamResult.isPartial() && streamResult.getContentRange() != null) {
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                response.setHeader("Content-Range", streamResult.getContentRange());
            }
            if (streamResult.getContentLength() > 0) {
                response.setContentLengthLong(streamResult.getContentLength());
            }
            streamResult.getInputStream().transferTo(response.getOutputStream());
            response.getOutputStream().flush();
        }
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<BaseResponse<List<LessonDto>>> getBySession(@PathVariable Long sessionId, HttpServletRequest req) {
        return ResponseEntity.ok(BaseResponse.ok(lessonService.getBySession(sessionId), "OK", req.getRequestURI()));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<BaseResponse<List<LessonDto>>> getByCourse(@PathVariable Long courseId, HttpServletRequest req) {
        return ResponseEntity.ok(BaseResponse.ok(lessonService.getByCourse(courseId), "OK", req.getRequestURI()));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<LessonDto>> create(@RequestBody LessonCreateRequest body, HttpServletRequest req) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(lessonService.create(body), "Lesson created", req.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(BaseResponse.error(e.getMessage(), "BAD_REQUEST", req.getRequestURI()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<LessonDto>> update(@PathVariable Long id, @RequestBody LessonCreateRequest body, HttpServletRequest req) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(lessonService.update(id, body), "Lesson updated", req.getRequestURI()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(BaseResponse.error(e.getMessage(), "NOT_FOUND", req.getRequestURI()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id, HttpServletRequest req) {
        lessonService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok(null, "Lesson deleted", req.getRequestURI()));
    }

    @PutMapping("/{id}/video")
    public ResponseEntity<BaseResponse<LessonDto>> saveVideo(@PathVariable Long id, @RequestBody LessonVideoRequest body, HttpServletRequest req) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(lessonService.saveVideo(id, body), "Video saved", req.getRequestURI()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(BaseResponse.error(e.getMessage(), "NOT_FOUND", req.getRequestURI()));
        }
    }

    @PostMapping("/{id}/ipa/regenerate")
    public ResponseEntity<BaseResponse<LessonDto>> regenerateIpa(@PathVariable Long id, HttpServletRequest req) {
        try {
            return ResponseEntity.ok(BaseResponse.ok(lessonService.regenerateIpa(id), "IPA regenerated", req.getRequestURI()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(BaseResponse.error(e.getMessage(), "BAD_REQUEST", req.getRequestURI()));
        }
    }
}

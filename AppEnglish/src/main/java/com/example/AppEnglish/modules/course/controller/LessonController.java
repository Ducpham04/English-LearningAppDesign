package com.example.AppEnglish.modules.course.controller;

import com.example.AppEnglish.common.dto.BaseResponse;
import com.example.AppEnglish.modules.course.dto.LessonDetailDTO;
import com.example.AppEnglish.modules.course.dto.LessonQuizDetailDTO;
import com.example.AppEnglish.modules.course.dto.LessonVideoDetailDTO;
import com.example.AppEnglish.modules.course.service.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/lessons")
@RequiredArgsConstructor
@Tag(name = "Lesson", description = "Lesson management APIs")
public class LessonController {

    private final LessonService lessonService;

    @GetMapping("/{lessonId}")
    @Operation(summary = "Get lesson details", description = "Get detailed lesson information (video with subtitles or quiz with hidden answers)")
    public ResponseEntity<BaseResponse<LessonDetailDTO>> getLessonDetail(
            @PathVariable Long lessonId,
            HttpServletRequest request) {

        LessonDetailDTO lessonDetail = lessonService.getLessonDetail(lessonId);

        return ResponseEntity.ok(BaseResponse.ok(lessonDetail, "Lesson details retrieved", request.getRequestURI()));
    }

    @GetMapping("/video")
    @Operation(summary = "Get all video lessons", description = "Get list of all video-type lessons with URL, subtitles, and target words")
    public ResponseEntity<BaseResponse<List<LessonVideoDetailDTO>>> getVideoLessons(
            HttpServletRequest request) {

        List<LessonVideoDetailDTO> videoLessons = lessonService.getVideoLessons();

        return ResponseEntity.ok(BaseResponse.ok(videoLessons, "Video lessons retrieved", request.getRequestURI()));
    }

    @GetMapping("/quiz")
    @Operation(summary = "Get all quiz lessons", description = "Get list of all quiz-type lessons with questions and options (answers hidden)")
    public ResponseEntity<BaseResponse<List<LessonQuizDetailDTO>>> getQuizLessons(
            HttpServletRequest request) {

        List<LessonQuizDetailDTO> quizLessons = lessonService.getQuizLessons();

        return ResponseEntity.ok(BaseResponse.ok(quizLessons, "Quiz lessons retrieved", request.getRequestURI()));
    }
}

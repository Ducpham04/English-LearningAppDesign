package com.example.appenglish.modules.transcribe.service.transcript;

import com.example.appenglish.common.exception.ApiException;
import com.example.appenglish.entity.Lesson;
import com.example.appenglish.entity.LessonVideo;
import com.example.appenglish.modules.transcribe.dto.TranscriptSentence;
import com.example.appenglish.repository.LessonRepository;
import com.example.appenglish.repository.LessonVideoRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonVideoTranscriptPersistenceService {

    LessonRepository lessonRepository;
    LessonVideoRepository lessonVideoRepository;

    @Transactional
    public void markProcessing(Long lessonId, String youtubeUrl, String s3Uri) {
        LessonVideo lessonVideo = getOrCreateLessonVideoLocked(lessonId);

        if (lessonVideo.getTranscriptJson() != null && !lessonVideo.getTranscriptJson().isEmpty()) {
            log.info("Skip markProcessing: transcript already exists for lessonId={}", lessonId);
            return;
        }

        lessonVideo.setVideoUrl(s3Uri);
    }

    @Transactional
    public void saveTranscriptForLessonVideo(Long lessonId,
                                             String youtubeUrl,
                                             String s3Uri,
                                             List<TranscriptSentence> sentences,
                                             String rawTranscriptJson) {
        LessonVideo lessonVideo = getOrCreateLessonVideoLocked(lessonId);

        if (lessonVideo.getTranscriptJson() != null && !lessonVideo.getTranscriptJson().isEmpty()) {
            log.info("Skip saveTranscript: transcript already exists for lessonId={}", lessonId);
            return;
        }

        lessonVideo.setVideoUrl(s3Uri);

        // Convert DTO List to Map List for JSONB storage
        List<Map<String, Object>> transcriptJson = new ArrayList<>(sentences != null ? sentences.size() : 0);
        if (sentences != null) {
            for (TranscriptSentence s : sentences) {
                Map<String, Object> item = new HashMap<>();
                item.put("start", s.getStartMs());
                item.put("end", s.getEndMs());
                item.put("text", s.getText());
                transcriptJson.add(item);
            }
        }

        lessonVideo.setTranscriptJson(transcriptJson);
        log.info("Saved transcript: lessonId={}, sentences={}", lessonId, transcriptJson.size());
    }

    @Transactional
    public void markFailed(Long lessonId, String youtubeUrl, String s3Uri, String reason) {
        LessonVideo lessonVideo = getOrCreateLessonVideoLocked(lessonId);

        // Nếu đã có transcript rồi thì không đánh dấu failed nữa
        if (lessonVideo.getTranscriptJson() != null && !lessonVideo.getTranscriptJson().isEmpty()) {
            log.info("Skip markFailed: transcript already exists for lessonId={}", lessonId);
            return;
        }

        if (s3Uri != null && !s3Uri.isBlank()) {
            lessonVideo.setVideoUrl(s3Uri);
        }
        log.error("Transcription failed: lessonId={}, reason={}", lessonId, reason);
    }

    private LessonVideo getOrCreateLessonVideoLocked(Long lessonId) {
        // 1. Kiểm tra Lesson tồn tại
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ApiException("Lesson not found", "LESSON_NOT_FOUND"));

        // 2. Fast path: Thử lấy và khóa record hiện có
        var existing = lessonVideoRepository.findByLessonIdForUpdate(lessonId);
        if (existing.isPresent()) {
            return existing.get();
        }

        // 3. Create path: Tạo mới nếu chưa có
        try {
            LessonVideo lv = new LessonVideo();
            lv.setLesson(lesson);
            // Default empty json to avoid null pointers if needed, or leave null
            lv.setVideoUrl(""); 
            
            lessonVideoRepository.saveAndFlush(lv);
        } catch (DataIntegrityViolationException e) {
            // Đã có transaction khác tạo rồi
            log.info("LessonVideo already created concurrently for lessonId={}, will re-lock", lessonId);
        }

        // 4. Luôn đọc lại với khóa Pessimistic để đảm bảo nhất quán
        return lessonVideoRepository.findByLessonIdForUpdate(lessonId)
                .orElseThrow(() -> new ApiException("Failed to create lesson video", "LESSON_VIDEO_CREATE_FAILED"));
    }
}
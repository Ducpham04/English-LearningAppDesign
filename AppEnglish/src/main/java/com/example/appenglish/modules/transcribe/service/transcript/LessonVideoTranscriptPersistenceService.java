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
        lessonVideo.setYoutubeUrl(youtubeUrl);
    }

    @Transactional
    public void saveTranscriptForLessonVideo(Long lessonId,
                                             String youtubeUrl,
                                             String s3Uri,
                                             List<TranscriptSentence> sentences,
                                             String rawTranscriptJson) {
        LessonVideo lessonVideo = getOrCreateLessonVideoLocked(lessonId);

        boolean hasExisting = lessonVideo.getTranscriptJson() != null && !lessonVideo.getTranscriptJson().isEmpty();

        lessonVideo.setVideoUrl(s3Uri);
        lessonVideo.setYoutubeUrl(youtubeUrl);

        List<Map<String, Object>> transcriptJson = new ArrayList<>(sentences != null ? sentences.size() : 0);
        if (sentences != null) {
            for (TranscriptSentence s : sentences) {
                Map<String, Object> item = new HashMap<>();
                item.put("sentenceIndex", s.getSentenceIndex());
                item.put("start", s.getStartMs());
                item.put("end", s.getEndMs());
                item.put("text", s.getText());
                if (s.getViText() != null && !s.getViText().isBlank()) {
                    item.put("viText", s.getViText());
                }
                item.put("ipa", s.getIpa());
                transcriptJson.add(item);
            }
        }

        if (hasExisting) {
            // Option A: If already exists, only update if new version has more translations or different content
            log.info("Transcript already exists for lessonId={}, updating with potentially new translations", lessonId);
        }

        lessonVideo.setTranscriptJson(transcriptJson);

        long translatedCount = transcriptJson.stream().filter(m -> m.get("viText") != null).count();
        log.info("Saved transcript: lessonId={}, sentences={}, translated={}", lessonId, transcriptJson.size(), translatedCount);
    }

    @Transactional
    public void markFailed(Long lessonId, String youtubeUrl, String s3Uri, String reason) {
        LessonVideo lessonVideo = getOrCreateLessonVideoLocked(lessonId);

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
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ApiException("Lesson not found", "LESSON_NOT_FOUND"));

        // 1) Fast path: lock existing row
        var existing = lessonVideoRepository.findByLessonIdForUpdate(lessonId);
        if (existing.isPresent()) {
            return existing.get();
        }

        // 2) Create path: insert without holding a detached instance around, then lock
        // NOTE: saveAndFlush + later re-select can lead to stale/detached merge issues under retries.
        try {
            LessonVideo lv = new LessonVideo();
            lv.setLesson(lesson);
            // @MapsId will copy lesson.id into lessonId when persisted
            lessonVideoRepository.save(lv);
            lessonVideoRepository.flush();
        } catch (DataIntegrityViolationException e) {
            // Another concurrent transaction inserted it first
            log.info("LessonVideo already created concurrently for lessonId={}, will re-lock", lessonId);
        }

        // Always re-read with the pessimistic lock as the single source of truth
        return lessonVideoRepository.findByLessonIdForUpdate(lessonId)
                .orElseThrow(() -> new ApiException("Failed to create lesson video", "LESSON_VIDEO_CREATE_FAILED"));
    }
}

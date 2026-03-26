package com.example.appenglish.modules.lesson.service;

import com.example.appenglish.entity.Lesson;
import com.example.appenglish.entity.LessonVideo;
import com.example.appenglish.entity.LessonVocabulary;
import com.example.appenglish.entity.Vocabulary;
import com.example.appenglish.enums.LessonType;
import com.example.appenglish.common.exception.ApiException;
import com.example.appenglish.common.exception.ConstraintMessageHelper;
import com.example.appenglish.modules.lesson.dto.LessonCreateRequest;
import com.example.appenglish.modules.lesson.dto.LessonDto;
import com.example.appenglish.modules.lesson.dto.LessonVocabularyUpsertRequest;
import com.example.appenglish.modules.lesson.dto.LessonVideoRequest;
import com.example.appenglish.modules.transcribe.service.transcript.EspeakIpaService;
import com.example.appenglish.modules.transcribe.service.s3.StorageService;
import com.example.appenglish.modules.vocabulary.service.VocabularyService;
import com.example.appenglish.repository.LessonRepository;
import com.example.appenglish.repository.LessonVideoRepository;
import com.example.appenglish.repository.LessonVocabularyRepository;
import com.example.appenglish.repository.VocabularyRepository;
import com.example.appenglish.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final SessionRepository sessionRepository;
    private final LessonVideoRepository lessonVideoRepository;
    private final EspeakIpaService espeakIpaService;
    private final StorageService storageService;
    private final VocabularyService vocabularyService;
    private final VocabularyRepository vocabularyRepository;
    private final LessonVocabularyRepository lessonVocabularyRepository;

    public List<LessonDto> getAll() {
        return lessonRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public LessonDto getById(Long id) {
        return lessonRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Lesson not found: " + id));
    }

    public List<LessonDto> getBySession(Long sessionId) {
        return lessonRepository.findBySessionIdOrderByOrderIndexAsc(sessionId).stream()
                .map(this::toDto).collect(Collectors.toList());
    }

    public List<LessonDto> getByCourse(Long courseId) {
        return lessonRepository.findByCourseIdOrderByOrderIndexAsc(courseId).stream()
                .map(this::toDto).collect(Collectors.toList());
    }

    /** Lessons from curricula (sessions) owned by this teacher — for assigning video lessons in class. */
    public List<LessonDto> getByTeacher(String teacherId) {
        return lessonRepository.findBySession_Course_Teacher_IdOrderByOrderIndexAsc(teacherId).stream()
                .map(this::toDto).collect(Collectors.toList());
    }

    public LessonDto create(LessonCreateRequest req) {
        Lesson lesson = new Lesson();
        lesson.setTitle(req.getTitle());
        lesson.setDescription(req.getDescription());
        lesson.setDurationMinutes(req.getDurationMinutes() != null ? req.getDurationMinutes() : 5);
        lesson.setOrderIndex(req.getOrderIndex() != null ? req.getOrderIndex() : 1);

        if (req.getType() != null) {
            try { lesson.setType(LessonType.valueOf(req.getType())); }
            catch (IllegalArgumentException ignored) { lesson.setType(LessonType.VIDEO); }
        } else {
            lesson.setType(LessonType.VIDEO);
        }

        if (req.getSessionId() != null) {
            sessionRepository.findById(req.getSessionId())
                    .ifPresent(lesson::setSession);
        }

        return toDto(lessonRepository.save(lesson));
    }

    public LessonDto update(Long id, LessonCreateRequest req) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found: " + id));
        if (req.getTitle() != null) lesson.setTitle(req.getTitle());
        if (req.getDescription() != null) lesson.setDescription(req.getDescription());
        if (req.getDurationMinutes() != null) lesson.setDurationMinutes(req.getDurationMinutes());
        if (req.getOrderIndex() != null) lesson.setOrderIndex(req.getOrderIndex());
        if (req.getType() != null) {
            try { lesson.setType(LessonType.valueOf(req.getType())); }
            catch (IllegalArgumentException ignored) {}
        }
        return toDto(lessonRepository.save(lesson));
    }

    @Transactional
    public List<Map<String, Object>> upsertVocabularies(Long lessonId, List<LessonVocabularyUpsertRequest> items) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found: " + lessonId));
        if (items == null || items.isEmpty()) {
            return List.of();
        }
        // Ensure lesson type is VOCAB when adding vocab content
        if (lesson.getType() == null || lesson.getType() != LessonType.VOCAB) {
            lesson.setType(LessonType.VOCAB);
            lessonRepository.save(lesson);
        }

        int idx = 0;
        for (LessonVocabularyUpsertRequest req : items) {
            if (req == null) continue;
            Integer orderIndex = req.getOrderIndex() != null ? req.getOrderIndex() : idx;

            Long vocabId = req.getVocabularyId();
            Vocabulary vocab;
            if (vocabId != null) {
                vocab = vocabularyRepository.findById(vocabId)
                        .orElseThrow(() -> new RuntimeException("Vocabulary not found: " + vocabId));
            } else {
                vocab = vocabularyService.findOrCreate(
                        req.getWord(),
                        req.getPhonetic(),
                        req.getMeaning(),
                        req.getExample(),
                        lessonId
                );
            }

            // Ensure link exists (in case vocabularyId path used)
            if (!lessonVocabularyRepository.existsByLesson_IdAndVocabulary_Id(lessonId, vocab.getId())) {
                LessonVocabulary lv = new LessonVocabulary();
                lv.setId(new LessonVocabulary.LessonVocabularyId(lessonId, vocab.getId()));
                lv.setLesson(lesson);
                lv.setVocabulary(vocab);
                lv.setOrderIndex(orderIndex);
                lessonVocabularyRepository.save(lv);
            }
            idx++;
        }

        return getLessonVocabularies(lessonId);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getLessonVocabularies(Long lessonId) {
        if (!lessonRepository.existsById(lessonId)) {
            throw new RuntimeException("Lesson not found: " + lessonId);
        }
        return lessonVocabularyRepository.findByLesson_IdOrderByOrderIndexAsc(lessonId).stream()
                .map(lv -> {
                    Vocabulary v = lv.getVocabulary();
                    return Map.<String, Object>of(
                            "vocabularyId", v.getId(),
                            "word", v.getWord(),
                            "phonetic", v.getPhonetic(),
                            "meanings", v.getMeanings(),
                            "orderIndex", lv.getOrderIndex() == null ? 0 : lv.getOrderIndex()
                    );
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        try {
            lessonRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            log.warn("Cannot delete lesson {}: {}", id, e.getMessage());
            String detail = ConstraintMessageHelper.buildDeleteConstraintMessage(e, "bài học");
            throw new ApiException(detail, "CONSTRAINT_VIOLATION");
        }
    }

    @Transactional
    public LessonDto regenerateIpa(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found: " + lessonId));

        LessonVideo lv = lessonVideoRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("No video/transcript for lesson: " + lessonId));

        List<Map<String, Object>> transcript = lv.getTranscriptJson();
        if (transcript == null || transcript.isEmpty()) {
            throw new RuntimeException("Transcript not yet available for lesson: " + lessonId);
        }

        int updated = 0;
        for (Map<String, Object> entry : transcript) {
            String text = entry.get("text") instanceof String ? (String) entry.get("text") : null;
            if (text == null || text.isBlank()) continue;
            try {
                String ipa = espeakIpaService.toIpa(text);
                if (ipa != null && !ipa.isBlank()) {
                    entry.put("ipa", ipa);
                    updated++;
                }
            } catch (Exception ex) {
                log.warn("IPA failed for lessonId={} text={}: {}", lessonId, text, ex.getMessage());
            }
        }

        lv.setTranscriptJson(transcript);
        lessonVideoRepository.save(lv);
        log.info("IPA regenerated for lessonId={}, updated={}/{}", lessonId, updated, transcript.size());
        return toDto(lesson);
    }

    public LessonDto saveVideo(Long lessonId, LessonVideoRequest req) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found: " + lessonId));

        LessonVideo lv = lessonVideoRepository.findById(lessonId).orElseGet(() -> {
            LessonVideo newLv = new LessonVideo();
            newLv.setLesson(lesson);
            return newLv;
        });
        if (req.getVideoUrl() != null) lv.setVideoUrl(req.getVideoUrl());
        if (req.getTranscriptJson() != null) lv.setTranscriptJson(req.getTranscriptJson());
        lessonVideoRepository.save(lv);

        lesson.setType(LessonType.VIDEO);
        lessonRepository.save(lesson);

        return toDto(lesson);
    }

    private LessonDto toDto(Lesson l) {
        LessonVideo lv = l.getVideo();
        boolean hasTranscript = lv != null && lv.getTranscriptJson() != null && !lv.getTranscriptJson().isEmpty();
        String s3Status = lv == null ? null
                : hasTranscript ? "COMPLETED"
                : (lv.getVideoUrl() != null ? "PROCESSING" : null);
        return LessonDto.builder()
                .id(l.getId())
                .title(l.getTitle())
                .description(l.getDescription())
                .type(l.getType() != null ? l.getType().name() : "VIDEO")
                .orderIndex(l.getOrderIndex())
                .durationMinutes(l.getDurationMinutes())
                .sessionId(l.getSession() != null ? l.getSession().getId() : null)
                .courseId(l.getCourse() != null ? l.getCourse().getId() : null)
                .videoUrl(lv != null && lv.getVideoUrl() != null ? storageService.getVideoPlaybackUrl(lv.getVideoUrl()) : null)
                .youtubeUrl(lv != null ? lv.getYoutubeUrl() : null)
                .s3Status(s3Status)
                .transcriptJson(lv != null ? lv.getTranscriptJson() : null)
                .build();
    }
}

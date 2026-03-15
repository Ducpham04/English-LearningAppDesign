package com.example.appenglish.modules.vocabulary.service;

import com.example.appenglish.entity.LearningStatus;
import com.example.appenglish.entity.Lesson;
import com.example.appenglish.entity.LessonVocabulary;
import com.example.appenglish.entity.UserVocabId;
import com.example.appenglish.entity.UserVocabularyProgress;
import com.example.appenglish.entity.Vocabulary;
import com.example.appenglish.modules.vocabulary.dto.AddToMyVocabularyRequest;
import com.example.appenglish.modules.vocabulary.dto.AddToMyVocabularyResponseDto;
import com.example.appenglish.modules.vocabulary.dto.MyVocabularyItemDto;
import com.example.appenglish.repository.LessonRepository;
import com.example.appenglish.repository.LessonVocabularyRepository;
import com.example.appenglish.repository.UserRepository;
import com.example.appenglish.repository.UserVocabularyProgressRepository;
import com.example.appenglish.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VocabularyService {

    private final VocabularyRepository vocabularyRepository;
    private final UserVocabularyProgressRepository userVocabularyProgressRepository;
    private final LessonVocabularyRepository lessonVocabularyRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    /**
     * Find vocabulary by word (ignore case); if not exists create one with given phonetic/meaning/example.
     * If lessonId is provided and the lesson exists, ensure LessonVocabulary link exists.
     */
    @Transactional
    public Vocabulary findOrCreate(String word, String phonetic, String meaning, String example, Long lessonId) {
        String normalized = word != null ? word.trim() : "";
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Word cannot be empty");
        }
        Optional<Vocabulary> existing = vocabularyRepository.findByWordIgnoreCase(normalized);
        if (existing.isPresent()) {
            Vocabulary v = existing.get();
            if (lessonId != null) {
                ensureLessonVocabularyLink(lessonId, v.getId());
            }
            return v;
        }
        Vocabulary v = new Vocabulary();
        v.setWord(normalized);
        v.setPhonetic(phonetic != null ? phonetic.trim() : null);
        String ex = (example != null && !example.isBlank()) ? example.trim() : "";
        if (meaning != null && !meaning.isBlank()) {
            List<Map<String, String>> meanings = new ArrayList<>();
            meanings.add(Map.of("pos", "general", "def", meaning.trim(), "ex", ex));
            v.setMeanings(meanings);
        }
        v = vocabularyRepository.save(v);
        if (lessonId != null) {
            ensureLessonVocabularyLink(lessonId, v.getId());
        }
        return v;
    }

    private void ensureLessonVocabularyLink(Long lessonId, Long vocabId) {
        if (!lessonRepository.existsById(lessonId)) {
            return;
        }
        if (lessonVocabularyRepository.existsByLesson_IdAndVocabulary_Id(lessonId, vocabId)) {
            return;
        }
        Lesson lesson = lessonRepository.getReferenceById(lessonId);
        Vocabulary vocabulary = vocabularyRepository.getReferenceById(vocabId);
        LessonVocabulary lv = new LessonVocabulary();
        lv.setId(new LessonVocabulary.LessonVocabularyId(lessonId, vocabId));
        lv.setLesson(lesson);
        lv.setVocabulary(vocabulary);
        lv.setOrderIndex(0);
        lessonVocabularyRepository.save(lv);
    }

    /**
     * Add a word to current user's vocabulary (find or create Vocabulary, then create UserVocabularyProgress if needed).
     */
    @Transactional
    public AddToMyVocabularyResponseDto addToMyVocabulary(String userId, AddToMyVocabularyRequest request) {
        if (request == null || request.getWord() == null || request.getWord().isBlank()) {
            throw new IllegalArgumentException("Word is required");
        }
        Vocabulary vocab = findOrCreate(
                request.getWord(),
                request.getPhonetic(),
                request.getMeaning(),
                request.getExample(),
                request.getLessonId()
        );
        boolean alreadySaved = userVocabularyProgressRepository.existsByUser_IdAndVocabulary_Id(userId, vocab.getId());
        if (!alreadySaved) {
            UserVocabularyProgress uvp = new UserVocabularyProgress();
            UserVocabId id = new UserVocabId();
            id.setUserId(userId);
            id.setVocabId(vocab.getId());
            uvp.setId(id);
            uvp.setUser(userRepository.getReferenceById(userId));
            uvp.setVocabulary(vocab);
            uvp.setStatus(LearningStatus.NEW);
            userVocabularyProgressRepository.save(uvp);
        }
        return AddToMyVocabularyResponseDto.builder()
                .vocabularyId(vocab.getId())
                .word(vocab.getWord())
                .alreadySaved(alreadySaved)
                .build();
    }

    /**
     * Remove a word from current user's vocabulary (delete UserVocabularyProgress only; Vocabulary remains).
     */
    @Transactional
    public void removeFromMyVocabulary(String userId, Long vocabularyId) {
        if (vocabularyId == null) {
            throw new IllegalArgumentException("Vocabulary ID is required");
        }
        UserVocabId id = new UserVocabId();
        id.setUserId(userId);
        id.setVocabId(vocabularyId);
        userVocabularyProgressRepository.deleteById(id);
    }

    /**
     * Search vocabularies by word (contains, ignore case). For FE lookup when user clicks a word.
     */
    public List<Vocabulary> search(String word) {
        if (word == null || word.isBlank()) {
            return Collections.emptyList();
        }
        return vocabularyRepository.findByWordContainingIgnoreCase(word.trim());
    }

    /**
     * Get current user's saved vocabulary, optionally filtered by lessonId or sessionId.
     */
    public List<MyVocabularyItemDto> getMyVocabulary(String userId, Long lessonId, Long sessionId) {
        List<UserVocabularyProgress> list;
        if (lessonId != null) {
            list = userVocabularyProgressRepository.findByUserIdAndLessonId(userId, lessonId);
        } else if (sessionId != null) {
            list = userVocabularyProgressRepository.findByUserIdAndSessionId(userId, sessionId);
        } else {
            list = userVocabularyProgressRepository.findByUserIdWithVocabulary(userId);
        }
        return list.stream()
                .map(uvp -> toMyVocabularyItemDto(uvp.getVocabulary()))
                .collect(Collectors.toList());
    }

    private static MyVocabularyItemDto toMyVocabularyItemDto(Vocabulary v) {
        String example = null;
        if (v.getMeanings() != null && !v.getMeanings().isEmpty()) {
            Object ex = v.getMeanings().get(0).get("ex");
            if (ex != null && !ex.toString().isBlank()) {
                example = ex.toString();
            }
        }
        return MyVocabularyItemDto.builder()
                .id(v.getId())
                .word(v.getWord())
                .phonetic(v.getPhonetic())
                .meanings(v.getMeanings() != null ? v.getMeanings() : Collections.emptyList())
                .example(example)
                .build();
    }
}

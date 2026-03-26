package com.example.appenglish.repository;

import com.example.appenglish.entity.LessonVocabulary;
import com.example.appenglish.entity.LessonVocabulary.LessonVocabularyId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonVocabularyRepository extends JpaRepository<LessonVocabulary, LessonVocabularyId> {

    boolean existsByLesson_IdAndVocabulary_Id(Long lessonId, Long vocabularyId);
    List<LessonVocabulary> findByLesson_IdOrderByOrderIndexAsc(Long lessonId);
    void deleteByLesson_Session_Id(Long sessionId);
}

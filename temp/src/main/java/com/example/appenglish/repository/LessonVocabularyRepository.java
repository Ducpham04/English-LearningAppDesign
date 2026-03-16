package com.example.appenglish.repository;

import com.example.appenglish.entity.LessonVocabulary;
import com.example.appenglish.entity.LessonVocabulary.LessonVocabularyId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonVocabularyRepository extends JpaRepository<LessonVocabulary, LessonVocabularyId> {

    boolean existsByLesson_IdAndVocabulary_Id(Long lessonId, Long vocabularyId);
    void deleteByLesson_Session_Id(Long sessionId);
}

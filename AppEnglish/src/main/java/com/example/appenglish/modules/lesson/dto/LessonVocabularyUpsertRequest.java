package com.example.appenglish.modules.lesson.dto;

import lombok.Data;

@Data
public class LessonVocabularyUpsertRequest {
  /** If provided, will link existing vocabulary id to the lesson. */
  private Long vocabularyId;

  /** If vocabularyId not provided, these fields are used to find-or-create vocabulary. */
  private String word;
  private String phonetic;
  private String meaning;
  private String example;

  /** Optional order in lesson. */
  private Integer orderIndex;
}


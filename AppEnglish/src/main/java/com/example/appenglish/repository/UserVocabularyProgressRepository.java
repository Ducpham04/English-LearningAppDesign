package com.example.appenglish.repository;

import com.example.appenglish.entity.UserVocabularyProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserVocabularyProgressRepository extends JpaRepository<UserVocabularyProgress, Long> {
    List<UserVocabularyProgress> findByUser_Id(String userId);
}

package com.example.appenglish.repository;

import com.example.appenglish.entity.TestOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestOptionRepository extends JpaRepository<TestOption, Long> {
    List<TestOption> findByQuestion_IdOrderByIdAsc(Long questionId);
    void deleteByQuestion_Test_Id(Long testId);
}

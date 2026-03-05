package com.example.AppEnglish.repository;

import com.example.AppEnglish.entity.TestOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestOptionRepository extends JpaRepository<TestOption, Long> {

    List<TestOption> findByQuestion_Id(Long questionId);
}

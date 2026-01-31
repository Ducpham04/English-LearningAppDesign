package com.example.appenglish.modules.vocabulary.config;

import com.example.appenglish.entity.Vocabulary;
import com.example.appenglish.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class VocabularyDataInitializer {

    @Bean
    public CommandLineRunner initializeVocabularyData(VocabularyRepository vocabularyRepository) {
        return args -> {
            if (vocabularyRepository.count() == 0) {
                // Tạo duy nhất 1 bản ghi mẫu dưới dạng Template
                Vocabulary template = Vocabulary.builder()
                        .word("hello")
                        .phonetic("/həˈləʊ/")
                        .definition("used as a greeting or to begin a telephone conversation.")
                        .example("Hello, Paul. I haven't seen you for ages.")
                        .level("A1")
                        .build();

                vocabularyRepository.save(template);
                
                log.info("✅ Đã khởi tạo dữ liệu mẫu cho bảng Vocabulary (ID: 1)");
            }
        };
    }
}
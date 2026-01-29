package com.example.appenglish.modules.vocabulary.config;

import com.example.appenglish.entity.Vocabulary;
import com.example.appenglish.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class VocabularyDataInitializer {

    @Bean
    public CommandLineRunner initializeVocabularyData(VocabularyRepository vocabularyRepository) {
        return args -> {
            if (vocabularyRepository.count() == 0) {
                // Tạo duy nhất 1 bản ghi mẫu dưới dạng Template
                Vocabulary template = Vocabulary.builder()
                        .word("string_placeholder")
                        .phonetic("phonetic_string")
                        .definition("definition_text")
                        .example("example_sentence_string")
                        .level("A1_TO_C2") // Mô tả các mức độ có thể nhập
                        .build();

                vocabularyRepository.save(template);
                
                System.out.println("✅ Khởi tạo dữ liệu mẫu Template (ID: 1)");
            }
        };
    }
}
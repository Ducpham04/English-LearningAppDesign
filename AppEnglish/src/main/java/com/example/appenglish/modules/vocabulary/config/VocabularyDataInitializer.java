package com.example.appenglish.modules.vocabulary.config;

import com.example.appenglish.entity.Vocabulary;
import com.example.appenglish.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/**
 * Khởi tạo dữ liệu mẫu cho Vocabulary.
 */
@Configuration
@RequiredArgsConstructor
public class VocabularyDataInitializer {

    /**
     * Tạo dữ liệu mẫu khi ứng dụng khởi động.
     */
    @Bean
    public CommandLineRunner initializeVocabularyData(VocabularyRepository vocabularyRepository) {
        return args -> {
            // Kiểm tra xem database đã có dữ liệu không
            if (vocabularyRepository.count() == 0) {
                // Tạo các từ vựng mẫu
                Vocabulary hello = Vocabulary.builder()
                        .word("hello")
                        .phonetic("həˈloʊ")
                        .audioUrl("https://example.com/audio/hello.mp3")
                        .meanings(List.of(
                                Map.of("pos", "noun", "def", "a polite expression used when meeting someone", 
                                       "ex", "She said hello to me.")
                        ))
                        .tags(List.of("A1", "Greetings", "IELTS"))
                        .build();

                Vocabulary world = Vocabulary.builder()
                        .word("world")
                        .phonetic("ˈwɜːrld")
                        .audioUrl("https://example.com/audio/world.mp3")
                        .meanings(List.of(
                                Map.of("pos", "noun", "def", "the earth and all its people and countries",
                                       "ex", "The world is round.")
                        ))
                        .tags(List.of("A1", "Geography", "IELTS"))
                        .build();

                Vocabulary beautiful = Vocabulary.builder()
                        .word("beautiful")
                        .phonetic("ˈbjuːtɪfl")
                        .audioUrl("https://example.com/audio/beautiful.mp3")
                        .meanings(List.of(
                                Map.of("pos", "adjective", "def", "pleasing to the eye or mind",
                                       "ex", "The sunset is beautiful.")
                        ))
                        .tags(List.of("A2", "Adjectives", "IELTS"))
                        .build();

                Vocabulary coffee = Vocabulary.builder()
                        .word("coffee")
                        .phonetic("ˈkɔːfi")
                        .audioUrl("https://example.com/audio/coffee.mp3")
                        .meanings(List.of(
                                Map.of("pos", "noun", "def", "a dark drink made from roasted coffee beans",
                                       "ex", "I drink coffee every morning.")
                        ))
                        .tags(List.of("A1", "Food", "Drinks"))
                        .build();

                Vocabulary knowledge = Vocabulary.builder()
                        .word("knowledge")
                        .phonetic("ˈnɑːlɪdʒ")
                        .audioUrl("https://example.com/audio/knowledge.mp3")
                        .meanings(List.of(
                                Map.of("pos", "noun", "def", "information and skills acquired through experience or education",
                                       "ex", "Knowledge is power.")
                        ))
                        .tags(List.of("B1", "Education", "IELTS"))
                        .build();

                // Lưu vào database
                vocabularyRepository.saveAll(List.of(hello, world, beautiful, coffee, knowledge));
                System.out.println("✅ Đã khởi tạo 5 từ vựng mẫu");
            }
        };
    }
}

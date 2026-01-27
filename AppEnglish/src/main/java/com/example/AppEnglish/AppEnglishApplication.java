package com.example.appenglish;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@ComponentScan(basePackages = {
    "com.example.appenglish",
    "com.example.appenglish.modules",
    "com.example.appenglish.modules.vocabulary"
})
public class AppEnglishApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppEnglishApplication.class, args);
    }

}
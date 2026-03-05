package com.example.AppEnglish;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AppEnglishApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppEnglishApplication.class, args);
    }

}
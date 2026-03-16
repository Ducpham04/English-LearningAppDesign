package com.example.appenglish;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.converter.json.GsonBuilderUtils;

@SpringBootApplication
@EnableJpaAuditing
public class AppEnglishApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppEnglishApplication.class, args);
        System.out.println("Hello ");
    }


}
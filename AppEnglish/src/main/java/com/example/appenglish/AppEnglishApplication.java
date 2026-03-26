package com.example.appenglish;

import com.example.appenglish.config.DockerHostServiceConnectionRewriteListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AppEnglishApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(AppEnglishApplication.class);
        // Sau Docker Compose / service connections: đổi kafka:29092 và db:5432 → localhost khi chạy trên máy
        app.addListeners(new DockerHostServiceConnectionRewriteListener());
        app.run(args);
    }
}
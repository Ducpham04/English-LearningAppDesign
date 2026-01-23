package com.example.appenglish.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("English AI Learning Platform API")
                        .description("Auto-generated API documentation (Swagger UI).")
                        .version("v1")
                        .license(new License().name("Private")));
    }
}


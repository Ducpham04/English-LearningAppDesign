package com.example.appenglish.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3.0 Configuration cho Swagger UI.
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "English Learning App API",
        version = "1.0.0",
        description = "API cho ứng dụng học tiếng Anh với hệ thống SRS",
        contact = @Contact(
            name = "Development Team",
            email = "dev@englishlearning.com"
        ),
        license = @License(
            name = "Apache 2.0",
            url = "https://www.apache.org/licenses/LICENSE-2.0.html"
        )
    ),
    servers = {
        @Server(
            url = "http://localhost:8080/api",
            description = "Local Development Server"
        )
    }
)
public class SwaggerConfig {
}

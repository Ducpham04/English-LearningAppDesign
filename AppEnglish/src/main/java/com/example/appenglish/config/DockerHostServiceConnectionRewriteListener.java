package com.example.appenglish.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;

/**
 * Spring Boot Docker Compose + service connections có thể inject:
 * <ul>
 *   <li>{@code kafka:29092} — chỉ resolve trong network Docker
 *   <li>{@code jdbc:postgresql://db:5432/...} — host {@code db} chỉ có trong compose
 * </ul>
 * Khi JVM chạy <strong>trên máy</strong> (IDE/mvn), cần {@code localhost} + port đã publish.
 * <p>
 * Chạy sau các listener khác ({@link Ordered#LOWEST_PRECEDENCE}) để ghi đè property từ compose.
 */
public class DockerHostServiceConnectionRewriteListener
    implements ApplicationListener<ApplicationEnvironmentPreparedEvent>, Ordered {

  private static final String KAFKA_PROP = "spring.kafka.bootstrap-servers";
  private static final String JDBC_PROP = "spring.datasource.url";

  private static boolean runningInDocker() {
    try {
      return Files.exists(Path.of("/.dockerenv"));
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
    if (runningInDocker()) {
      return;
    }
    ConfigurableEnvironment env = event.getEnvironment();
    Map<String, Object> overrides = new HashMap<>();

    String kafka = env.getProperty(KAFKA_PROP);
    if (!StringUtils.hasText(kafka)) {
      overrides.put(KAFKA_PROP, "localhost:9092");
    } else if (kafka.contains("kafka:29092")) {
      overrides.put(KAFKA_PROP, kafka.replace("kafka:29092", "localhost:9092"));
    }

    String jdbc = env.getProperty(JDBC_PROP);
    if (StringUtils.hasText(jdbc) && jdbc.contains("://db:5432")) {
      overrides.put(JDBC_PROP, jdbc.replace("://db:5432", "://localhost:5432"));
    }

    if (!overrides.isEmpty()) {
      env.getPropertySources()
          .addFirst(new MapPropertySource("dockerHostServiceConnectionRewrite", overrides));
    }
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}

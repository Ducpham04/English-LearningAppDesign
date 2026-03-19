package com.example.appenglish.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;

/**
 * Guards against a common misconfiguration:
 * SPRING_KAFKA_BOOTSTRAP_SERVERS="" (empty) overrides defaults and crashes Kafka consumer startup.
 *
 * If either spring.kafka.bootstrap-servers or SPRING_KAFKA_BOOTSTRAP_SERVERS resolves to blank,
 * we force a safe fallback to localhost:9092.
 */
public class KafkaBootstrapFallbackEnvironmentPostProcessor
    implements EnvironmentPostProcessor, Ordered {

  private static final String FALLBACK = "localhost:9092";
  private static final String PROPERTY = "spring.kafka.bootstrap-servers";

  @Override
  public void postProcessEnvironment(
      ConfigurableEnvironment environment, SpringApplication application) {
    String resolved = environment.getProperty(PROPERTY);
    if (StringUtils.hasText(resolved)) return;

    Map<String, Object> props = new HashMap<>();
    props.put(PROPERTY, FALLBACK);
    environment
        .getPropertySources()
        .addFirst(new MapPropertySource("kafkaBootstrapServersFallback", props));
  }

  @Override
  public int getOrder() {
    // High precedence: we want to override empty values early.
    return Ordered.HIGHEST_PRECEDENCE;
  }
}


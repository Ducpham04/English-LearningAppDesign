# ===== Build stage =====
# Use Maven with JDK 21 to compile and package the Spring Boot application
FROM maven:3.9-eclipse-temurin-21 AS build

# Working directory inside the build container
WORKDIR /workspace

# Copy Maven descriptor first to leverage Docker layer caching
COPY pom.xml .

# Copy application source code
COPY src ./src

# Build the application and create the runnable jar (skip tests for faster build)
RUN mvn -q -DskipTests clean package

# ===== Runtime stage =====
# Use a smaller JRE-only image for running the application
FROM eclipse-temurin:21-jre

# Install required system dependencies used by the application
# - ffmpeg: audio/video processing
# - espeak-ng: text-to-speech/IPA processing
RUN apt-get update \
    && apt-get install -y --no-install-recommends ffmpeg espeak-ng \
    && rm -rf /var/lib/apt/lists/*

# Working directory for the running app
WORKDIR /app

# Copy the built jar from the build stage into the runtime image
COPY --from=build /workspace/target/*.jar /app/app.jar

# Default temp directory (can be overridden by APP_TEMP_DIR env var)
RUN mkdir -p /app/temp

# Expose the Spring Boot default port
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

package com.example.appenglish.modules.transcribe.service.youtube;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class YoutubeDownloaderService {

    private static final String DEFAULT_CONTAINER_NAME = "langmaster-ytdlp";

    @Value("${app.temp-dir:temp}")
    String tempDir;

    private Path resolveTempDirPath() {
        Path configured = Paths.get(tempDir);
        if (configured.isAbsolute()) {
            return configured;
        }
        return Paths.get(System.getProperty("user.dir"), configured.toString()).normalize();
    }

    public File downloadAudio(String videoUrl) throws IOException, InterruptedException {
        String uniqueId = UUID.randomUUID().toString();

        // 1) Download inside container into /data
        String containerDir = "/data/" + uniqueId;
        String dockerOutputTemplate = containerDir + "/audio.%(ext)s";

        // Ensure directory exists in container (prevents yt-dlp writing elsewhere / failing silently)
        ProcessBuilder mkdirPb = new ProcessBuilder(
                "docker",
                "exec",
                DEFAULT_CONTAINER_NAME,
                "sh",
                "-c",
                "mkdir -p " + containerDir
        );
        log.info("Executing command: {}", String.join(" ", mkdirPb.command()));
        Process mkdirProc = mkdirPb.start();
        int mkdirExit = mkdirProc.waitFor();
        try (BufferedReader mkdirErr = new BufferedReader(new InputStreamReader(mkdirProc.getErrorStream()))) {
            if (mkdirExit != 0) {
                String mkdirStderr = mkdirErr.lines().collect(Collectors.joining("\n"));
                throw new IOException("Failed to create container dir " + containerDir + ". stderr: " + mkdirStderr);
            }
        }

        ProcessBuilder downloadPb = new ProcessBuilder(
                "docker",
                "exec",
                DEFAULT_CONTAINER_NAME,
                "/opt/venv/bin/yt-dlp",
                "--no-playlist",
                "--extract-audio",
                "--audio-format", "mp3",
                "--output", dockerOutputTemplate,
                videoUrl
        );

        log.info("Executing command: {}", String.join(" ", downloadPb.command()));

        Process downloadProc = downloadPb.start();
        int downloadExit = downloadProc.waitFor();
        String downloadStdout;
        String downloadStderr;
        try (BufferedReader out = new BufferedReader(new InputStreamReader(downloadProc.getInputStream()));
             BufferedReader err = new BufferedReader(new InputStreamReader(downloadProc.getErrorStream()))) {
            downloadStdout = out.lines().collect(Collectors.joining("\n"));
            downloadStderr = err.lines().collect(Collectors.joining("\n"));
        }
        if (downloadExit != 0) {
            log.error("yt-dlp failed with exit code {}. stdout: \n{}\nstderr: \n{}", downloadExit, downloadStdout, downloadStderr);
            throw new IOException("Failed to download audio from YouTube. yt-dlp exit code: " + downloadExit);
        }

        // 2) The container /data is bind-mounted to host ./temp/youtube-audio (docker-compose.yml)
        // so the downloaded file is already on the host filesystem. We only need to wait for it.
        Path baseTempDir = resolveTempDirPath();
        Path localMp3 = baseTempDir
                .resolve(Paths.get("youtube-audio", uniqueId, "audio.mp3"));

        long deadlineMs = System.currentTimeMillis() + 20_000;
        long lastSize = -1;
        int stableCount = 0;

        while (System.currentTimeMillis() < deadlineMs) {
            if (Files.exists(localMp3)) {
                long size;
                try {
                    size = Files.size(localMp3);
                } catch (IOException e) {
                    size = -1;
                }

                if (size > 0 && size == lastSize) {
                    stableCount++;
                    if (stableCount >= 3) {
                        break;
                    }
                } else {
                    stableCount = 0;
                }
                lastSize = size;
            }
            Thread.sleep(250);
        }

        if (!Files.exists(localMp3)) {
            throw new IOException("Downloaded file not found on host (bind mount). Expected: " + localMp3);
        }

        long finalSize = Files.size(localMp3);
        if (finalSize <= 0) {
            throw new IOException("Downloaded file is empty on host (bind mount). Path: " + localMp3);
        }

        log.info("Successfully downloaded audio to: {} ({} bytes)", localMp3.toAbsolutePath(), finalSize);
        return localMp3.toFile();
    }

    public void cleanup(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        Path filePath = file.toPath();
        Path parentDir = filePath.getParent();

        try {
            Files.delete(filePath);
            log.info("Cleaned up file: {}", filePath);
            if (parentDir != null && Files.isDirectory(parentDir)) {
                try (var stream = Files.list(parentDir)) {
                    List<Path> remaining = stream.toList();
                    if (remaining.isEmpty()) {
                        Files.delete(parentDir);
                        log.info("Cleaned up directory: {}", parentDir);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error during cleanup for file: {}", filePath, e);
        }
    }
}

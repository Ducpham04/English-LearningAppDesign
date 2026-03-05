package com.example.AppEnglish.modules.transcribe.service.transcript;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EspeakIpaService {

    @Value("${ipa.espeak.command:espeak-ng}")
    String espeakCommand;

    @Value("${ipa.espeak.voice:en-us}")
    String voice;

    public String toIpa(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        try {
            String command = espeakCommand;
            // Normalize path for Windows/Linux
            if (command.contains("/") || command.contains("\\")) {
                command = command.replace("/", java.io.File.separator).replace("\\", java.io.File.separator);
            }

            java.io.File exeFile = new java.io.File(command);
            if (!exeFile.isAbsolute()) {
                exeFile = new java.io.File(System.getProperty("user.dir"), command);
            }

            if (command.contains(java.io.File.separator)) {
                if (!exeFile.exists()) {
                    log.error("eSpeak-ng binary NOT FOUND at: {}. Please check your tools folder.", exeFile.getAbsolutePath());
                    return "";
                }
                command = exeFile.getAbsolutePath();
            }

            // If portable bundle is used, make sure eSpeak can locate its data.
            // For Windows portable, data folder usually is alongside exe: espeak-ng-data/
            if (exeFile.exists() && exeFile.getParentFile() != null) {
                java.io.File dataDir = new java.io.File(exeFile.getParentFile(), "espeak-ng-data");
                if (dataDir.exists() && dataDir.isDirectory()) {
                    // Both are commonly recognized by eSpeak NG builds.
                    // (Some builds use ESPEAK_DATA_PATH, others ESPEAKNG_DATA_PATH)
                    // Setting both is harmless.
                    // Note: ProcessBuilder env is per-process.
                }
            }

            List<String> cmd = new ArrayList<>();
            cmd.add(command);
            cmd.add("-q");
            cmd.add("--ipa=3");
            cmd.add("-v");
            cmd.add(voice);
            cmd.add(text);

            ProcessBuilder pb = new ProcessBuilder(cmd);
            if (exeFile.exists() && exeFile.getParentFile() != null) {
                pb.directory(exeFile.getParentFile());
                
                // Set data path environment variables for portable bundle
                java.io.File dataDir = new java.io.File(exeFile.getParentFile(), "espeak-ng-data");
                if (dataDir.exists() && dataDir.isDirectory()) {
                    String dataPath = dataDir.getAbsolutePath();
                    pb.environment().put("ESPEAK_DATA_PATH", dataPath);
                    pb.environment().put("ESPEAKNG_DATA_PATH", dataPath);
                }
            }
            pb.redirectErrorStream(true);
            Process p = pb.start();

            String out;
            // Use UTF-8 explicitly to read IPA characters correctly
            try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                out = r.lines().collect(java.util.stream.Collectors.joining(" ")).trim();
            }

            int exit = p.waitFor();
            if (exit != 0) {
                log.warn("eSpeak-ng exited with code {} for text={}", exit, text);
                return "";
            }

            return out;
        } catch (Exception e) {
            log.warn("Failed to generate IPA via eSpeak-ng for text={}. error={}", text, e.getMessage());
            return "";
        }
    }
}


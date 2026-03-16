package com.example.appenglish.modules.pronunciation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class AudioFfmpegFileTranscoder {

    @Value("${app.ffmpeg.path:ffmpeg}")
    private String ffmpegPath;

    public FileTranscodeResult transcodeFileToPcm16kMono(File inputFile) throws IOException {
        List<String> cmd = new ArrayList<>();
        cmd.add(ffmpegPath);
        cmd.add("-hide_banner");
        cmd.add("-loglevel");
        cmd.add("error");
        cmd.add("-i");
        cmd.add(inputFile.getAbsolutePath());
        cmd.add("-f");
        cmd.add("s16le");
        cmd.add("-ac");
        cmd.add("1");
        cmd.add("-ar");
        cmd.add("16000");
        cmd.add("pipe:1");

        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process p = pb.start();

        CompletableFuture<String> errFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return new String(p.getErrorStream().readAllBytes());
            } catch (IOException e) {
                return "Failed to read stderr: " + e.getMessage();
            }
        });

        return new FileTranscodeResult(p, p.getInputStream(), errFuture);
    }

    public record FileTranscodeResult(Process process, InputStream pcmStream, CompletableFuture<String> stderrFuture) {

        public int waitForExitCode(long timeoutMillis) {
            try {
                boolean exited = process.waitFor(timeoutMillis, java.util.concurrent.TimeUnit.MILLISECONDS);
                if (!exited) {
                    process.destroyForcibly();
                    return -1;
                }
                return process.exitValue();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return -1;
            }
        }

        public String stderr() {
            return stderrFuture.join();
        }
    }
}

package com.example.appenglish.modules.pronunciation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AudioFfmpegTranscoder {

    @Value("${app.ffmpeg.path:ffmpeg}")
    private String ffmpegPath;

    /** Thời gian chờ ffmpeg xử lý xong sau khi đã ghi hết dữ liệu vào stdin (giây). */
    @Value("${app.pronunciation.ffmpeg.wait-timeout-seconds:45}")
    private long waitTimeoutSeconds;

    public TranscodeResult transcodeToPcm16kMono(InputStream input) throws IOException, InterruptedException {
        List<String> cmd = new ArrayList<>();
        cmd.add(ffmpegPath);
        cmd.add("-hide_banner");
        cmd.add("-loglevel");
        cmd.add("error");
        cmd.add("-i");
        cmd.add("pipe:0");
        cmd.add("-f");
        cmd.add("s16le");
        cmd.add("-ac");
        cmd.add("1");
        cmd.add("-ar");
        cmd.add("16000");
        cmd.add("pipe:1");

        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process p = pb.start();

        // Read stderr in background to avoid deadlock
        CompletableFuture<String> errFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return new String(p.getErrorStream().readAllBytes());
            } catch (IOException e) {
                return "Failed to read stderr: " + e.getMessage();
            }
        });

        // Write stdin in current thread (synchronous)
        long bytesWritten = 0;
        try (OutputStream os = p.getOutputStream()) {
            bytesWritten = input.transferTo(os);
            log.info("Fed {} bytes to ffmpeg stdin", bytesWritten);
        } catch (IOException e) {
            log.error("ffmpeg stdin write failed after {} bytes", bytesWritten, e);
            p.destroy();
            throw e;
        }

        boolean exited = p.waitFor(waitTimeoutSeconds, TimeUnit.SECONDS);
        if (!exited) {
            p.destroyForcibly();
            throw new IOException("ffmpeg process timed out after " + waitTimeoutSeconds + "s");
        }

        String err = errFuture.join();
        return new TranscodeResult(p.getInputStream(), p.exitValue(), err, bytesWritten);
    }

    public record TranscodeResult(InputStream pcmStream, int exitCode, String error, long bytesWritten) {
    }
}

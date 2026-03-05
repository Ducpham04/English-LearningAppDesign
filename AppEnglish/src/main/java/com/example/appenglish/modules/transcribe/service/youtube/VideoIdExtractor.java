package com.example.AppEnglish.modules.transcribe.service.youtube;

import com.example.AppEnglish.common.exception.ApiException;
import org.springframework.stereotype.Component;

@Component
public class VideoIdExtractor {

    public String extractVideoId(String youtubeUrl) {
        if (youtubeUrl == null || youtubeUrl.isBlank()) {
            throw new ApiException("YouTube URL is invalid", "YOUTUBE_URL_INVALID");
        }

        if (youtubeUrl.contains("v=")) {
            String vPart = youtubeUrl.split("v=")[1];
            return vPart.contains("&") ? vPart.split("&")[0] : vPart;
        }

        if (youtubeUrl.contains("youtu.be/")) {
            String idPart = youtubeUrl.split("youtu.be/")[1];
            return idPart.contains("?") ? idPart.split("\\?")[0] : idPart;
        }

        if (youtubeUrl.contains("youtube.com/embed/")) {
            String[] parts = youtubeUrl.split("youtube\\.com/embed/");
            if (parts.length > 1) {
                String after = parts[1];
                return after.contains("?") ? after.split("\\?")[0] : after;
            }
        }

        if (youtubeUrl.contains("youtube.com/shorts/")) {
            String[] parts = youtubeUrl.split("shorts/");
            if (parts.length > 1) {
                String after = parts[1];
                return after.contains("?") ? after.split("\\?")[0] : after;
            }
        }

        throw new ApiException("YouTube URL is invalid", "YOUTUBE_URL_INVALID");
    }
}


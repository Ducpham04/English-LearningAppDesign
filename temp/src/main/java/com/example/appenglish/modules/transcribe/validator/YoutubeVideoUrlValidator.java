package com.example.appenglish.modules.transcribe.validator;

import com.example.appenglish.modules.transcribe.service.youtube.VideoIdExtractor;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class YoutubeVideoUrlValidator implements ConstraintValidator<ValidYoutubeVideoUrl, String> {

    private final VideoIdExtractor videoIdExtractor = new VideoIdExtractor();

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        try {
            videoIdExtractor.extractVideoId(value);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }
}


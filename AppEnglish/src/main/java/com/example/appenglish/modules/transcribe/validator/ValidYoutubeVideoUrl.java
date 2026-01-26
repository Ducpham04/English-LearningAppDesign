package com.example.appenglish.modules.transcribe.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = YoutubeVideoUrlValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidYoutubeVideoUrl {
    String message() default "youtubeUrl must be a YouTube video URL (watch?v=... or youtu.be/..., embed, shorts)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}


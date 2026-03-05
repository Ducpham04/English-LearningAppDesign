package com.example.AppEnglish.modules.transcribe.dto.request;

import com.example.AppEnglish.modules.transcribe.validator.ValidYoutubeVideoUrl;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YoutubeTranscribeRequest {

    @NotNull(message = "lessonId cannot be null")
    private Long lessonId;

    @NotBlank(message = "Youtube URL cannot be blank")
    @ValidYoutubeVideoUrl
    private String youtubeUrl;
}


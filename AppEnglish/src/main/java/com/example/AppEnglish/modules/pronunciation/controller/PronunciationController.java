package com.example.AppEnglish.modules.pronunciation.controller;

import com.example.AppEnglish.common.dto.BaseResponse;
import com.example.AppEnglish.entity.User;
import com.example.AppEnglish.modules.pronunciation.dto.PronunciationScoreResponse;
import com.example.AppEnglish.modules.pronunciation.service.PronunciationAttemptService;
import com.example.AppEnglish.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/pronunciation")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PronunciationController {

    PronunciationAttemptService attemptService;
    UserRepository userRepository;

    @PostMapping(value = "/score", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<PronunciationScoreResponse> score(
            Authentication authentication,
            @RequestParam @NotNull Long lessonId,
            @RequestParam @NotNull Integer sentenceIndex,
            @RequestPart("file") MultipartFile file
    ) throws Exception {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

        PronunciationScoreResponse response = attemptService.scoreAndEnqueueUpload(user, lessonId, sentenceIndex, file);
        return BaseResponse.ok(response, "OK", "/api/pronunciation/score");
    }
}


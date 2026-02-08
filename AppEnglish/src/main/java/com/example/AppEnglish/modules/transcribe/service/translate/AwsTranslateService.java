package com.example.appenglish.modules.transcribe.service.translate;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.TranslateTextRequest;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AwsTranslateService {

    TranslateClient translateClient;

    public String translateToVietnamese(String text, String sourceLanguageCode) {
        if (text == null || text.isBlank()) {
            return text;
        }

        String source = (sourceLanguageCode == null || sourceLanguageCode.isBlank()) ? "en" : sourceLanguageCode;

        return translateClient.translateText(TranslateTextRequest.builder()
                        .sourceLanguageCode(source)
                        .targetLanguageCode("vi")
                        .text(text)
                        .build())
                .translatedText();
    }
}


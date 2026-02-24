package com.example.appenglish.modules.pronunciation.service;

import com.example.appenglish.entity.LessonVideo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TranscriptSentenceResolver {

    public String resolveExpectedText(LessonVideo lessonVideo, int sentenceIndex) {
        List<Map<String, Object>> transcript = lessonVideo.getTranscriptJson();
        if (transcript == null) {
            throw new IllegalArgumentException("Transcript is null");
        }
        if (sentenceIndex < 0 || sentenceIndex >= transcript.size()) {
            throw new IllegalArgumentException("sentenceIndex out of range: " + sentenceIndex);
        }

        Map<String, Object> sentence = transcript.get(sentenceIndex);
        Object textObj = sentence.get("text");
        if (textObj == null) {
            throw new IllegalArgumentException("Transcript sentence missing 'text' at index: " + sentenceIndex);
        }

        String text = String.valueOf(textObj).trim();
        if (text.isBlank()) {
            throw new IllegalArgumentException("Expected text is blank at index: " + sentenceIndex);
        }

        return text;
    }
}


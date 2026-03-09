package com.example.appenglish.modules.ai.service;

import com.example.appenglish.modules.ai.dto.AiChatRequest;
import com.example.appenglish.modules.ai.dto.AiChatResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Gọi API chatbot AI (OpenAI Chat Completions hoặc API tùy chỉnh qua cấu hình).
 * Cấu hình: app.ai.chat.url (nếu set thì dùng API của bạn), không set thì dùng OpenAI.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatService {

    private static final String OPENAI_CHAT_URL = "https://api.openai.com/v1/chat/completions";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final ObjectMapper objectMapper;
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    @Value("${openai.api.key:}")
    private String openAiApiKey;

    @Value("${app.ai.chat.url:}")
    private String customChatUrl;

    @Value("${app.ai.chat.api-key:}")
    private String customChatApiKey;

    @Value("${app.ai.chat.model:gpt-4o-mini}")
    private String model;

    public AiChatResponse chat(AiChatRequest request) throws Exception {
        String url = (customChatUrl != null && !customChatUrl.isBlank()) ? customChatUrl : OPENAI_CHAT_URL;
        String apiKey = (customChatUrl != null && !customChatUrl.isBlank() && customChatApiKey != null && !customChatApiKey.isBlank())
                ? customChatApiKey
                : openAiApiKey;

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("AI Chat API key is not configured. Set openai.api.key or app.ai.chat.api-key.");
        }

        ObjectNode body = objectMapper.createObjectNode();
        body.put("model", model);

        ArrayNode messagesArray = objectMapper.createArrayNode();
        if (request.getLessonContext() != null && !request.getLessonContext().isBlank()) {
            ObjectNode systemMsg = objectMapper.createObjectNode();
            systemMsg.put("role", "system");
            systemMsg.put("content",
                    "You are a friendly English conversation practice partner. "
                            + "Context: " + request.getLessonContext() + ". "
                            + "Keep replies short (1-3 sentences). "
                            + "If the user writes in Vietnamese, you may reply in Vietnamese or English. "
                            + "Encourage speaking in English when possible.");
            messagesArray.add(systemMsg);
        }
        for (AiChatRequest.ChatMessageDto m : request.getMessages()) {
            ObjectNode msg = objectMapper.createObjectNode();
            msg.put("role", m.getRole() != null ? m.getRole() : "user");
            msg.put("content", m.getContent() != null ? m.getContent() : "");
            messagesArray.add(msg);
        }
        body.set("messages", messagesArray);

        RequestBody requestBody = RequestBody.create(objectMapper.writeValueAsString(body), JSON);
        Request httpRequest = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                String errBody = response.body() != null ? response.body().string() : "";
                log.warn("AI Chat API error: {} - {}", response.code(), errBody);
                throw new RuntimeException("AI Chat failed: " + response.code() + " - " + errBody);
            }
            String responseBody = response.body() != null ? response.body().string() : "{}";
            JsonNode root = objectMapper.readTree(responseBody);

            if (url.equals(OPENAI_CHAT_URL)) {
                JsonNode choices = root.path("choices");
                if (choices.isArray() && choices.size() > 0) {
                    String content = choices.get(0).path("message").path("content").asText("");
                    return AiChatResponse.builder().content(content.trim()).build();
                }
            } else {
                if (root.has("content")) {
                    return AiChatResponse.builder().content(root.path("content").asText("").trim()).build();
                }
                if (root.has("choices") && root.path("choices").isArray() && root.path("choices").size() > 0) {
                    String content = root.path("choices").get(0).path("message").path("content").asText("");
                    return AiChatResponse.builder().content(content.trim()).build();
                }
            }
            throw new RuntimeException("Unexpected AI Chat response format");
        }
    }
}

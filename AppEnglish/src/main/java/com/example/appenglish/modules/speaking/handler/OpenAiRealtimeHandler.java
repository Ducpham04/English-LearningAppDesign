package com.example.appenglish.modules.speaking.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handler for real-time AI speaking sessions using OpenAI Realtime API.
 * This class acts as a bridge between the Browser (WebSocket) and OpenAI (WebSocket).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiRealtimeHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final Map<String, WebSocket> openAiWebSockets = new ConcurrentHashMap<>();
    private final OkHttpClient okHttpClient = new OkHttpClient();

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Value("${openai.realtime.model:gpt-4o-realtime-preview-2024-10-01}")
    private String modelId;

    private static final String OPENAI_REALTIME_BASE_URL = "wss://api.openai.com/v1/realtime";

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Browser client connected: {}", session.getId());

        String url = OPENAI_REALTIME_BASE_URL + "?model=" + modelId;
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + openAiApiKey)
                .addHeader("OpenAI-Beta", "realtime=v1")
                .build();

        WebSocket openAiWs = okHttpClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                log.info("Connected to OpenAI Realtime API for session: {}", session.getId());
                sendInitialSessionUpdate(webSocket);
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                try {
                    JsonNode node = objectMapper.readTree(text);
                    String type = node.path("type").asText();

            // Forward relevant messages to client
            if ("response.audio.delta".equals(type)) {
                String base64Audio = node.path("delta").asText();
                if (session.isOpen()) {
                    session.sendMessage(new BinaryMessage(Base64.getDecoder().decode(base64Audio)));
                }
            } else if ("conversation.item.created".equals(type) ||
                       "response.created".equals(type) ||
                       "response.done".equals(type) ||
                       "response.audio_transcript.delta".equals(type) || 
                       "conversation.item.input_audio_transcription.completed".equals(type) ||
                       "input_audio_buffer.speech_started".equals(type) ||
                       "error".equals(type)) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(text));
                }
            }
                } catch (Exception e) {
                    log.error("Error processing OpenAI message for session {}", session.getId(), e);
                }
            }

            @Override
            public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                log.warn("OpenAI is closing: {} - {}", code, reason);
            }

            @Override
            public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
                log.error("OpenAI WebSocket failure for session {}", session.getId(), t);
                try {
                    session.close(CloseStatus.SERVER_ERROR);
                } catch (IOException e) {
                    log.error("Failed to close browser session", e);
                }
            }
        });

        openAiWebSockets.put(session.getId(), openAiWs);
    }

    private void sendInitialSessionUpdate(WebSocket webSocket) {
        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("type", "session.update");
            ObjectNode sessionNode = root.putObject("session");
            sessionNode.putArray("modalities").add("text").add("audio");
            sessionNode.put("instructions", 
                "You are a friendly English speaking buddy. " +
                "You can understand Vietnamese and English. " +
                "If the user speaks Vietnamese, reply in Vietnamese. " +
                "If the user speaks English, reply in English. " +
                "Keep replies short (1-2 sentences) and conversational. " +
                "Encourage the user to speak more by asking open questions.");
            sessionNode.put("voice", "alloy");
            sessionNode.put("input_audio_format", "pcm16");
            sessionNode.put("output_audio_format", "pcm16");
            
            // Enable user transcriptions
            ObjectNode transcription = objectMapper.createObjectNode();
            transcription.put("model", "whisper-1");
            sessionNode.set("input_audio_transcription", transcription);
            
            ObjectNode turnDetection = objectMapper.createObjectNode();
            turnDetection.put("type", "server_vad");
            turnDetection.put("threshold", 0.5);
            turnDetection.put("prefix_padding_ms", 300);
            turnDetection.put("silence_duration_ms", 800);
            sessionNode.set("turn_detection", turnDetection);

            webSocket.send(objectMapper.writeValueAsString(root));
            log.info("Sent session.update to OpenAI");
        } catch (Exception e) {
            log.error("Failed to send initial session update", e);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        WebSocket openAiWs = openAiWebSockets.get(session.getId());
        if (openAiWs != null) {
            openAiWs.send(message.getPayload());
        }
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        WebSocket openAiWs = openAiWebSockets.get(session.getId());
        if (openAiWs != null) {
            try {
                String base64Audio = Base64.getEncoder().encodeToString(message.getPayload().array());
                ObjectNode root = objectMapper.createObjectNode();
                root.put("type", "input_audio_buffer.append");
                root.put("audio", base64Audio);
                openAiWs.send(objectMapper.writeValueAsString(root));
            } catch (Exception e) {
                log.error("Error sending audio chunk to OpenAI", e);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Browser connection closed: {}", session.getId());
        WebSocket ws = openAiWebSockets.remove(session.getId());
        if (ws != null) {
            ws.close(1000, "Client disconnected");
        }
    }
}

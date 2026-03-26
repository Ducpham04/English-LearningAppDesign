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
    private final Map<String, Integer> audioBufferBytes = new ConcurrentHashMap<>();
    private final Map<String, Boolean> speechActive = new ConcurrentHashMap<>();
    private final Map<String, Boolean> responseActive = new ConcurrentHashMap<>();
    private final OkHttpClient okHttpClient = new OkHttpClient();


    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Value("${openai.realtime.model:gpt-4o-realtime-preview-2024-10-01}")
    private String modelId;

    /** Higher = less sensitive to background noise (fewer false speech starts). Typical 0.45–0.7 */
    @Value("${openai.realtime.vad.threshold:0.58}")
    private double vadThreshold;

    @Value("${openai.realtime.vad.silence-duration-ms:950}")
    private int vadSilenceDurationMs;

    @Value("${openai.realtime.vad.prefix-padding-ms:400}")
    private int vadPrefixPaddingMs;

    @Value("${openai.realtime.transcription.model:whisper-1}")
    private String transcriptionModel;

    private static final String OPENAI_REALTIME_BASE_URL = "wss://api.openai.com/v1/realtime";

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Browser client connected: {}", session.getId());
        audioBufferBytes.put(session.getId(), 0);
        speechActive.put(session.getId(), false);
        responseActive.put(session.getId(), false);

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
                audioBufferBytes.put(session.getId(), 0);
                speechActive.put(session.getId(), false);
                responseActive.put(session.getId(), false);
                sendInitialSessionUpdate(webSocket);
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                try {
                    if (!session.isOpen()) {
                        webSocket.close(1000, "Browser session closed");
                        return;
                    }

                    log.debug("OpenAI event for session {}: {}", session.getId(), text);
                    JsonNode node = objectMapper.readTree(text);
                    String type = node.path("type").asText();

            // Forward relevant messages to client
            if ("response.audio.delta".equals(type)) {
                String base64Audio = node.path("delta").asText();
                if (session.isOpen()) {
                    session.sendMessage(new BinaryMessage(Base64.getDecoder().decode(base64Audio)));
                }
            } else if ("response.error".equals(type)) {
                // Mark response as inactive if OpenAI reports a response error.
                responseActive.put(session.getId(), false);
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(text));
                }
            } else if ("response.cancelled".equals(type)) {
                responseActive.put(session.getId(), false);
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(text));
                }
            } else if ("response.done".equals(type)) {
                // Keep buffer management tied to speech events; just forward to client.
                audioBufferBytes.put(session.getId(), 0);
                speechActive.put(session.getId(), false);
                responseActive.put(session.getId(), false);
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(text));
                }
            } else if ("response.created".equals(type)) {
                responseActive.put(session.getId(), true);
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(text));
                }
            } else if ("input_audio_buffer.speech_started".equals(type)) {
                speechActive.put(session.getId(), true);
                if (responseActive.getOrDefault(session.getId(), false)) {
                    try {
                        ObjectNode cancel = objectMapper.createObjectNode();
                        cancel.put("type", "response.cancel");
                        webSocket.send(objectMapper.writeValueAsString(cancel));
                    } catch (Exception e) {
                        log.warn("Failed to send response.cancel on barge-in", e);
                    }
                }
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(text));
                }
            } else if ("input_audio_buffer.speech_stopped".equals(type)) {
                // User finished speaking -> commit and ask OpenAI to create a response now.
                audioBufferBytes.put(session.getId(), 0);
                try {
                    ObjectNode commit = objectMapper.createObjectNode();
                    commit.put("type", "input_audio_buffer.commit");
                    webSocket.send(objectMapper.writeValueAsString(commit));

                    ObjectNode create = objectMapper.createObjectNode();
                    create.put("type", "response.create");
                    webSocket.send(objectMapper.writeValueAsString(create));
                } catch (Exception e) {
                    log.warn("Failed to create response after speech_stopped", e);
                }
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(text));
                }
            } else if ("input_audio_buffer.committed".equals(type)) {
                // Forward commit acknowledgment
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(text));
                }
            } else if ("conversation.item.created".equals(type) ||
                       "response.audio_transcript.delta".equals(type) || 
                       "conversation.item.input_audio_transcription.completed".equals(type) ||
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
            transcription.put("model", transcriptionModel);
            sessionNode.set("input_audio_transcription", transcription);
            
            ObjectNode turnDetection = objectMapper.createObjectNode();
            turnDetection.put("type", "server_vad");
            turnDetection.put("threshold", vadThreshold);
            turnDetection.put("prefix_padding_ms", vadPrefixPaddingMs);
            turnDetection.put("silence_duration_ms", vadSilenceDurationMs);
            // Let the server create responses only after user finishes speaking.
            turnDetection.put("create_response", false);
            turnDetection.put("interrupt_response", true);
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
            try {
                JsonNode node = objectMapper.readTree(message.getPayload());
                String type = node.path("type").asText("");
                if ("response.create".equals(type)) {
                    boolean responding = responseActive.getOrDefault(session.getId(), false);
                    if (responding) {
                        log.debug("Skip response.create from client because response is active");
                        return;
                    }
                    responseActive.put(session.getId(), true);
                } else if ("response.cancel".equals(type)) {
                    responseActive.put(session.getId(), false);
                }
            } catch (Exception e) {
                log.debug("Failed to parse client event for response tracking", e);
            }
            openAiWs.send(message.getPayload());
        }
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        WebSocket openAiWs = openAiWebSockets.get(session.getId());
        if (openAiWs != null) {
            try {
                int size = message.getPayloadLength();
                audioBufferBytes.merge(session.getId(), size, Integer::sum);

                byte[] audioBytes = new byte[size];
                message.getPayload().get(audioBytes);
                String base64Audio = Base64.getEncoder().encodeToString(audioBytes);
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
        audioBufferBytes.remove(session.getId());
        speechActive.remove(session.getId());
        responseActive.remove(session.getId());
        WebSocket ws = openAiWebSockets.remove(session.getId());
        if (ws != null) {
            ws.close(1000, "Client disconnected");
        }
    }
}

package com.example.AppEnglish.modules.speaking.config;

import com.example.AppEnglish.modules.speaking.handler.OpenAiRealtimeHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final OpenAiRealtimeHandler openAiRealtimeHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(openAiRealtimeHandler, "/v1/speaking/realtime")
                .setAllowedOrigins("*");
    }
}

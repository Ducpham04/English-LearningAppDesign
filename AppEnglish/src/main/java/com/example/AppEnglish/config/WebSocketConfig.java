package com.example.appenglish.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Cấu hình broker gửi/nhận tin nhắn
        config.enableSimpleBroker("/topic", "/queue"); // Cho phép các subscription ở topic hoặc queue
        config.setApplicationDestinationPrefixes("/app"); // Prefix cho các endpoint gửi tin nhắn
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoints cho client WebSocket kết nối vào server
        registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS(); // Hỗ trợ SockJS
    }
}

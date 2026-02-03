package com.example.appenglish.modules.auth.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {
    @MessageMapping("/send-message") // Endpoint nhận tin nhắn từ client
    @SendTo("/topic/messages") // Tin nhắn sẽ được gửi đến client subscribe ở topic này
    public String processMessage(String message) {
        System.out.println("Message received: " + message);
        return "Server Response: " + message; // Phản hồi lại client
    }
}

package com.example.appenglish.modules.ai.controller;

import com.example.appenglish.common.dto.BaseResponse;
import com.example.appenglish.modules.ai.dto.AiChatRequest;
import com.example.appenglish.modules.ai.dto.AiChatResponse;
import com.example.appenglish.modules.ai.service.AiChatService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/ai")
@RequiredArgsConstructor
public class AiChatController {

    private final AiChatService aiChatService;

    @PostMapping("/chat")
    public ResponseEntity<BaseResponse<AiChatResponse>> chat(
            @Valid @RequestBody AiChatRequest request,
            HttpServletRequest httpRequest) {
        try {
            AiChatResponse response = aiChatService.chat(request);
            return ResponseEntity.ok(
                    BaseResponse.ok(response, "OK", httpRequest.getRequestURI())
            );
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.error(e.getMessage(), "CONFIG_ERROR", httpRequest.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(BaseResponse.error(e.getMessage(), "AI_CHAT_ERROR", httpRequest.getRequestURI()));
        }
    }
}

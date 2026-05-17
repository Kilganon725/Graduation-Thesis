package com.example.aifomo.controller;

import com.example.aifomo.common.ApiResponse;
import com.example.aifomo.dto.ChatRequest;
import com.example.aifomo.entity.AiChat;
import com.example.aifomo.service.AiChatService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final AiChatService aiChatService;

    public ChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping
    public ApiResponse<AiChat> chat(Authentication authentication, @RequestBody @Valid ChatRequest request) {
        return ApiResponse.success(aiChatService.chat(authentication.getName(), request.getQuestion()));
    }

    @GetMapping("/history")
    public ApiResponse<List<AiChat>> history(Authentication authentication, @RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.success(aiChatService.history(authentication.getName(), limit));
    }
}

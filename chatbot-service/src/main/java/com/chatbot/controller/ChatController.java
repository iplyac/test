package com.chatbot.controller;

import com.chatbot.model.ChatRequest;
import com.chatbot.model.ChatResponse;
import com.chatbot.service.OpenAIService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final OpenAIService openAIService;

    public ChatController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        if (request.getUserId() == null || request.getMessage() == null) {
            return ResponseEntity.badRequest().build();
        }
        ChatResponse response = openAIService.sendMessage(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/thread/{userId}")
    public ResponseEntity<Map<String, String>> resetThread(@PathVariable String userId) {
        openAIService.resetThread(userId);
        return ResponseEntity.ok(Map.of("message", "Thread reset successfully", "userId", userId));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "chatbot-service"));
    }
}

package com.telegram.service;

import com.telegram.model.ChatRequest;
import com.telegram.model.ChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ChatbotClient {

    private static final Logger log = LoggerFactory.getLogger(ChatbotClient.class);

    private final WebClient webClient;

    public ChatbotClient(@Value("${chatbot.service.url}") String chatbotServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(chatbotServiceUrl)
                .build();
    }

    public ChatResponse sendMessage(String userId, String message) {
        try {
            ChatRequest request = new ChatRequest(userId, message);

            return webClient.post()
                    .uri("/api/chat")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ChatResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("Error calling chatbot service", e);
            return new ChatResponse("Sorry, I'm having trouble connecting to the chatbot service.", null);
        }
    }

    public void resetThread(String userId) {
        try {
            webClient.delete()
                    .uri("/api/thread/{userId}", userId)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            log.info("Reset thread for user: {}", userId);
        } catch (Exception e) {
            log.error("Error resetting thread", e);
        }
    }
}

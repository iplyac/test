package com.chatbot.service;

import com.chatbot.model.ChatRequest;
import com.chatbot.model.ChatResponse;
import com.theokanning.openai.assistants.Assistant;
import com.theokanning.openai.assistants.AssistantRequest;
import com.theokanning.openai.assistants.ModifyAssistantRequest;
import com.theokanning.openai.messages.Message;
import com.theokanning.openai.messages.MessageRequest;
import com.theokanning.openai.runs.Run;
import com.theokanning.openai.runs.RunCreateRequest;
import com.theokanning.openai.threads.Thread;
import com.theokanning.openai.threads.ThreadRequest;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpenAIService {

    private final OpenAiService openAiService;
    private final PersonaLoaderService personaLoaderService;

    @Value("${openai.model:gpt-4-turbo-preview}")
    private String model;

    @Value("${openai.assistant.name:Chatbot Assistant}")
    private String assistantName;

    private String assistantId;
    private final Map<String, String> userThreads = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        createOrUpdateAssistant();
    }

    private void createOrUpdateAssistant() {
        try {
            String persona = personaLoaderService.getCurrentPersona();

            AssistantRequest assistantRequest = AssistantRequest.builder()
                    .model(model)
                    .name(assistantName)
                    .instructions(persona)
                    .build();

            Assistant assistant = openAiService.createAssistant(assistantRequest);
            assistantId = assistant.getId();
            log.info("Created assistant with ID: {}", assistantId);
        } catch (Exception e) {
            log.error("Error creating assistant", e);
            throw new RuntimeException("Failed to initialize OpenAI assistant", e);
        }
    }

    public ChatResponse sendMessage(ChatRequest request) {
        try {
            // Get or create thread for user
            String threadId = userThreads.computeIfAbsent(request.getUserId(), userId -> {
                Thread thread = openAiService.createThread(ThreadRequest.builder().build());
                log.info("Created new thread {} for user {}", thread.getId(), userId);
                return thread.getId();
            });

            // Add user message to thread
            MessageRequest messageRequest = MessageRequest.builder()
                    .role("user")
                    .content(request.getMessage())
                    .build();
            openAiService.createMessage(threadId, messageRequest);

            // Run the assistant
            RunCreateRequest runRequest = RunCreateRequest.builder()
                    .assistantId(assistantId)
                    .build();
            Run run = openAiService.createRun(threadId, runRequest);

            // Poll for completion
            Run completedRun = waitForRunCompletion(threadId, run.getId());

            if (!"completed".equals(completedRun.getStatus())) {
                log.error("Run failed with status: {}", completedRun.getStatus());
                return new ChatResponse("Sorry, I encountered an error processing your message.", threadId);
            }

            // Get the assistant's response
            List<Message> messages = openAiService.listMessages(threadId).getData();
            String response = messages.isEmpty() ? "No response"
                    : messages.get(0).getContent().get(0).getText().getValue();

            return new ChatResponse(response, threadId);

        } catch (Exception e) {
            log.error("Error processing message", e);
            return new ChatResponse("Sorry, an error occurred: " + e.getMessage(), null);
        }
    }

    private Run waitForRunCompletion(String threadId, String runId) throws InterruptedException {
        Run run;
        int maxAttempts = 60; // 60 seconds max wait
        int attempts = 0;

        do {
            java.lang.Thread.sleep(1000);
            run = openAiService.retrieveRun(threadId, runId);
            attempts++;

            if (attempts >= maxAttempts) {
                log.error("Run timed out after {} seconds", maxAttempts);
                break;
            }
        } while (!"completed".equals(run.getStatus()) &&
                !"failed".equals(run.getStatus()) &&
                !"cancelled".equals(run.getStatus()));

        return run;
    }

    public void resetThread(String userId) {
        String removedThreadId = userThreads.remove(userId);
        if (removedThreadId != null) {
            log.info("Reset thread {} for user {}", removedThreadId, userId);
        }
    }

    public void updateAssistantPersona() {
        try {
            String newPersona = personaLoaderService.getCurrentPersona();
            openAiService.modifyAssistant(assistantId,
                    ModifyAssistantRequest.builder()
                            .model(model)
                            .name(assistantName)
                            .instructions(newPersona)
                            .build());
            log.info("Updated assistant persona");
        } catch (Exception e) {
            log.error("Error updating assistant persona", e);
        }
    }
}

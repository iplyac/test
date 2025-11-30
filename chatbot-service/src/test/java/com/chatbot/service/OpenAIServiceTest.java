package com.chatbot.service;

import com.chatbot.model.ChatRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for OpenAIService.
 * Note: Full integration testing with OpenAI SDK is complex due to the SDK's
 * structure.
 * These tests focus on the service's internal logic and state management.
 */
@ExtendWith(MockitoExtension.class)
class OpenAIServiceTest {

    @Mock
    private com.theokanning.openai.service.OpenAiService openAiService;

    @Mock
    private PersonaLoaderService personaLoaderService;

    @InjectMocks
    private com.chatbot.service.OpenAIService openAIService;

    private static final String TEST_USER_ID = "test-user-123";
    private static final String TEST_MESSAGE = "Hello, bot!";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(openAIService, "model", "gpt-4-turbo-preview");
        ReflectionTestUtils.setField(openAIService, "assistantName", "Test Assistant");
    }

    @Test
    void testResetThread() {
        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> openAIService.resetThread(TEST_USER_ID));
    }

    @Test
    void testChatRequest_ValidInput() {
        // Arrange
        ChatRequest request = new ChatRequest(TEST_USER_ID, TEST_MESSAGE);

        // Assert
        assertNotNull(request);
        assertEquals(TEST_USER_ID, request.getUserId());
        assertEquals(TEST_MESSAGE, request.getMessage());
    }
}

package com.telegram.service;

import com.telegram.model.ChatResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ChatbotClientTest {

    private MockWebServer mockWebServer;
    private ChatbotClient chatbotClient;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = mockWebServer.url("/").toString();

        // Create ChatbotClient and inject WebClient
        chatbotClient = new ChatbotClient(baseUrl);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testSendMessage_Success() {
        // Arrange
        String responseBody = "{\"response\":\"Hello!\",\"threadId\":\"thread_123\"}";
        mockWebServer.enqueue(new MockResponse()
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json"));

        // Act
        ChatResponse response = chatbotClient.sendMessage("user123", "Hello");

        // Assert
        assertNotNull(response);
        assertEquals("Hello!", response.getResponse());
        assertEquals("thread_123", response.getThreadId());
    }

    @Test
    void testSendMessage_ServerError() {
        // Arrange
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        // Act
        ChatResponse response = chatbotClient.sendMessage("user123", "Hello");

        // Assert
        assertNotNull(response);
        assertTrue(response.getResponse().contains("trouble connecting"));
    }

    @Test
    void testResetThread_Success() {
        // Arrange
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        // Act & Assert
        assertDoesNotThrow(() -> chatbotClient.resetThread("user123"));
    }

    @Test
    void testResetThread_ServerError() {
        // Arrange
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> chatbotClient.resetThread("user123"));
    }
}

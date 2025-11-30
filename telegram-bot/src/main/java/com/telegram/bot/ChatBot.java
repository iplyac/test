package com.telegram.bot;

import com.telegram.model.ChatResponse;
import com.telegram.service.ChatbotClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class ChatBot extends TelegramLongPollingBot {

    private final ChatbotClient chatbotClient;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    public ChatBot(ChatbotClient chatbotClient,
            @Value("${telegram.bot.token}") String botToken) {
        super(botToken);
        this.chatbotClient = chatbotClient;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        String messageText = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();
        String userId = update.getMessage().getFrom().getId().toString();

        log.info("Received message from user {}: {}", userId, messageText);

        try {
            if (messageText.startsWith("/")) {
                handleCommand(chatId, userId, messageText);
            } else {
                handleMessage(chatId, userId, messageText);
            }
        } catch (Exception e) {
            log.error("Error processing update", e);
            sendResponse(chatId, "Sorry, an error occurred while processing your message.");
        }
    }

    private void handleCommand(String chatId, String userId, String command) {
        switch (command.toLowerCase().split(" ")[0]) {
            case "/start":
                sendResponse(chatId,
                        "👋 Hello! I'm an AI chatbot powered by OpenAI.\n\n" +
                                "You can:\n" +
                                "• Send me any message to chat\n" +
                                "• Use /reset to start a new conversation\n" +
                                "• Use /help to see this message again\n\n" +
                                "Let's chat!");
                break;

            case "/help":
                sendResponse(chatId,
                        "📚 Available commands:\n\n" +
                                "/start - Start the bot\n" +
                                "/help - Show this help message\n" +
                                "/reset - Reset conversation history\n\n" +
                                "Just send me a message to start chatting!");
                break;

            case "/reset":
                chatbotClient.resetThread(userId);
                sendResponse(chatId, "🔄 Conversation reset! Let's start fresh.");
                break;

            default:
                sendResponse(chatId, "Unknown command. Use /help to see available commands.");
        }
    }

    private void handleMessage(String chatId, String userId, String messageText) {
        // Send typing action
        try {
            execute(SendMessage.builder()
                    .chatId(chatId)
                    .text("...")
                    .build());
        } catch (TelegramApiException e) {
            log.warn("Could not send typing indicator", e);
        }

        // Get response from chatbot service
        ChatResponse response = chatbotClient.sendMessage(userId, messageText);

        if (response != null && response.getResponse() != null) {
            sendResponse(chatId, response.getResponse());
        } else {
            sendResponse(chatId, "Sorry, I couldn't process your message right now.");
        }
    }

    private void sendResponse(String chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error sending message", e);
        }
    }
}

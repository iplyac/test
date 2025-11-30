package com.chatbot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class PersonaLoaderService {

    private static final Logger log = LoggerFactory.getLogger(PersonaLoaderService.class);

    @Value("${chatbot.persona.file-path:./persona.txt}")
    private String personaFilePath;

    private final AtomicReference<String> currentPersona = new AtomicReference<>("");
    private long lastModifiedTime = 0;

    @PostConstruct
    public void init() {
        loadPersona();
    }

    @Scheduled(fixedDelay = 60000) // Poll every 60 seconds
    public void checkAndReloadPersona() {
        try {
            Path path = Paths.get(personaFilePath);
            if (!Files.exists(path)) {
                log.warn("Persona file not found: {}", personaFilePath);
                return;
            }

            long currentModifiedTime = Files.getLastModifiedTime(path).toMillis();
            if (currentModifiedTime > lastModifiedTime) {
                loadPersona();
                log.info("Persona file reloaded due to changes");
            }
        } catch (IOException e) {
            log.error("Error checking persona file modification time", e);
        }
    }

    private void loadPersona() {
        try {
            Path path = Paths.get(personaFilePath);
            if (Files.exists(path)) {
                String persona = Files.readString(path);
                currentPersona.set(persona);
                lastModifiedTime = Files.getLastModifiedTime(path).toMillis();
                log.info("Persona loaded successfully from: {}", personaFilePath);
            } else {
                log.warn("Persona file not found: {}, using default persona", personaFilePath);
                currentPersona.set("You are a helpful AI assistant.");
            }
        } catch (IOException e) {
            log.error("Error loading persona file", e);
            currentPersona.set("You are a helpful AI assistant.");
        }
    }

    public String getCurrentPersona() {
        return currentPersona.get();
    }
}

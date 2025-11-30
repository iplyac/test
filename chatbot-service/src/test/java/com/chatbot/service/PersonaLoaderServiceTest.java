package com.chatbot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PersonaLoaderServiceTest {

    private PersonaLoaderService personaLoaderService;

    @TempDir
    Path tempDir;

    private Path personaFile;

    @BeforeEach
    void setUp() throws IOException {
        personaFile = tempDir.resolve("persona.txt");
        Files.writeString(personaFile, "Initial persona content");

        personaLoaderService = new PersonaLoaderService();
        ReflectionTestUtils.setField(personaLoaderService, "personaFilePath", personaFile.toString());
    }

    @Test
    void testLoadPersona_Success() {
        // Act
        ReflectionTestUtils.invokeMethod(personaLoaderService, "loadPersona");
        String persona = personaLoaderService.getCurrentPersona();

        // Assert
        assertNotNull(persona);
        assertEquals("Initial persona content", persona);
    }

    @Test
    void testGetCurrentPersona_DefaultValue() {
        // Arrange
        PersonaLoaderService newService = new PersonaLoaderService();
        ReflectionTestUtils.setField(newService, "personaFilePath", "nonexistent.txt");

        // Act
        String persona = newService.getCurrentPersona();

        // Assert
        assertNotNull(persona);
        // Default persona is empty string when file doesn't exist
        assertEquals("", persona);
    }

    @Test
    void testCheckAndReloadPersona_FileModified() throws IOException, InterruptedException {
        // Arrange
        ReflectionTestUtils.invokeMethod(personaLoaderService, "loadPersona");
        String initialPersona = personaLoaderService.getCurrentPersona();

        // Wait a bit to ensure file modification time changes
        Thread.sleep(100);

        // Modify file
        Files.writeString(personaFile, "Updated persona content");

        // Act
        personaLoaderService.checkAndReloadPersona();
        String updatedPersona = personaLoaderService.getCurrentPersona();

        // Assert
        assertNotEquals(initialPersona, updatedPersona);
        assertEquals("Updated persona content", updatedPersona);
    }

    @Test
    void testCheckAndReloadPersona_FileNotModified() {
        // Arrange
        ReflectionTestUtils.invokeMethod(personaLoaderService, "loadPersona");
        String initialPersona = personaLoaderService.getCurrentPersona();

        // Act
        personaLoaderService.checkAndReloadPersona();
        String persona = personaLoaderService.getCurrentPersona();

        // Assert
        assertEquals(initialPersona, persona);
    }

    @Test
    void testCheckAndReloadPersona_FileNotFound() {
        // Arrange
        PersonaLoaderService newService = new PersonaLoaderService();
        ReflectionTestUtils.setField(newService, "personaFilePath", "nonexistent.txt");

        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> newService.checkAndReloadPersona());
    }
}

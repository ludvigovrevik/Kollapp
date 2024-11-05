package core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


import java.time.LocalDateTime;

/**
 * Unit tests for the {@link Message} class.
 */
@Tag("message")
public class MessageTest {

    private Message message;

    @BeforeEach
    @DisplayName("Initialize Message object before each test")
    void setUp() {
        message = new Message("Author", "Hello, World!");
    }

    @Test
    @DisplayName("Test default constructor for Jackson")
    @Tag("constructor")
    void testDefaultConstructor() {
        Message defaultMessage = new Message();
        assertNotNull(defaultMessage, "Message object should not be null");
    }

    @Test
    @DisplayName("Test parameterized constructor sets values")
    @Tag("constructor")
    void testParameterizedConstructor() {
        assertEquals("Author", message.getAuthor(), "Author should match the initialized value");
        assertEquals("Hello, World!", message.getText(), "Text should match the initialized value");
        assertNotNull(message.getTimestamp(), "Timestamp should be automatically initialized");
        assertTrue(message.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)), "Timestamp should be set to current time");
    }

    @Test
    @DisplayName("Test setting author")
    @Tag("setter")
    void testSetAuthor() {
        message.setAuthor("New Author");
        assertEquals("New Author", message.getAuthor(), "Author should be updated to the new value");
    }

    @Test
    @DisplayName("Test setting null author throws exception")
    @Tag("setter")
    void testSetNullAuthor() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            message.setAuthor(null);
        }, "Setting a null author should throw IllegalArgumentException");

        assertEquals("Author cannot be null.", exception.getMessage());
    }

    @Test
    @DisplayName("Test setting text")
    @Tag("setter")
    void testSetText() {
        message.setText("New Text");
        assertEquals("New Text", message.getText(), "Text should be updated to the new value");
    }

    @Test
    @DisplayName("Test setting null text throws exception")
    @Tag("setter")
    void testSetNullText() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            message.setText(null);
        }, "Setting a null text should throw IllegalArgumentException");

        assertEquals("Text cannot be null.", exception.getMessage());
    }

    @Test
    @DisplayName("Test getTimestamp")
    @Tag("getter")
    void testGetTimestamp() {
        LocalDateTime timestamp = message.getTimestamp();
        assertNotNull(timestamp, "Timestamp should not be null");
        assertTrue(timestamp.isBefore(LocalDateTime.now().plusSeconds(1)), "Timestamp should be valid and set to initialization time");
    }
}

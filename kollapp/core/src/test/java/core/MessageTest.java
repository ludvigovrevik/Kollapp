package core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    @Nested
    @DisplayName("Tests for equals method")
    @Tag("equals")
    class EqualsTest {
        
        @Test
        @DisplayName("Test equals with same object reference")
        void testEqualsSameObject() {
            assertTrue(message.equals(message), "Message should be equal to itself");
        }

        @Test
        @DisplayName("Test equals with null object")
        void testEqualsNullObject() {
            assertFalse(message.equals(null), "Message should not be equal to null");
        }

        @Test
        @DisplayName("Test equals with different class")
        void testEqualsDifferentClass() {
            Object differentClass = new Object();
            assertFalse(message.equals(differentClass), "Message should not be equal to object of different class");
        }

        @Test
        @DisplayName("Test equals with identical message")
        void testEqualsIdenticalMessage() {
            Message identicalMessage = new Message("Author", "Hello, World!");
            // Set the same timestamp to ensure complete equality
            identicalMessage.setTimestamp(message.getTimestamp());
            assertTrue(message.equals(identicalMessage), "Messages with same content should be equal");
        }

        @Test
        @DisplayName("Test equals with different author")
        void testEqualsDifferentAuthor() {
            Message differentAuthor = new Message("Different Author", "Hello, World!");
            differentAuthor.setTimestamp(message.getTimestamp());
            assertFalse(message.equals(differentAuthor), "Messages with different authors should not be equal");
        }

        @Test
        @DisplayName("Test equals with different text")
        void testEqualsDifferentText() {
            Message differentText = new Message("Author", "Different text");
            differentText.setTimestamp(message.getTimestamp());
            assertFalse(message.equals(differentText), "Messages with different texts should not be equal");
        }

        @Test
        @DisplayName("Test equals with different timestamp")
        void testEqualsDifferentTimestamp() {
            Message differentTimestamp = new Message("Author", "Hello, World!");
            differentTimestamp.setTimestamp(LocalDateTime.now().plusHours(1));
            assertFalse(message.equals(differentTimestamp), "Messages with different timestamps should not be equal");
        }

        @Test
        @DisplayName("Test equals with null fields")
        void testEqualsNullFields() {
            Message messageWithNullFields = new Message();
            Message anotherMessageWithNullFields = new Message();
            
            // Both messages have all null fields
            assertTrue(messageWithNullFields.equals(anotherMessageWithNullFields), 
                "Messages with all null fields should be equal");
        }

        @Test
        @DisplayName("Test equals with mixed null fields")
        void testEqualsMixedNullFields() {
            Message messageWithSomeNulls = new Message();
            messageWithSomeNulls.setAuthor("Author");
            // text and timestamp remain null
            
            Message anotherMessageWithSomeNulls = new Message();
            anotherMessageWithSomeNulls.setAuthor("Author");
            // text and timestamp remain null
            
            assertTrue(messageWithSomeNulls.equals(anotherMessageWithSomeNulls), 
                "Messages with same non-null and null fields should be equal");
            
            // Set one field different
            anotherMessageWithSomeNulls.setText("Some text");
            assertFalse(messageWithSomeNulls.equals(anotherMessageWithSomeNulls), 
                "Messages with different mix of null fields should not be equal");
        }
    }
}

package core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Unit tests for the {@link GroupChat} class.
 */
@Tag("groupChat")
public class GroupChatTest {

    private GroupChat groupChat;

    @BeforeEach
    @DisplayName("Initialize GroupChat object before each test")
    void setUp() {
        this.groupChat = new GroupChat();
    }

    @Test
    @DisplayName("Test GroupChats default constructor")
    @Tag("constructor")
    void testGroupChatConstructor() {
        assertNotNull(this.groupChat, "GroupChat object should not be null");
        assertTrue(this.groupChat.getMessages().isEmpty(), "New GroupChat should have no messages");
    }

    @Test
    @DisplayName("Test adding a message")
    @Tag("add")
    void testAddMessage() {
        Message message = new Message("User", "Hello World");
        
        this.groupChat.addMessage(message);
        List<Message> messages = groupChat.getMessages();

        assertEquals(1, messages.size(), "There should be 1 message in the group chat");
        assertTrue(messages.contains(message), "Messages should contain the added message");
    }

    @Test
    @DisplayName("Test adding a null message throws exception")
    @Tag("add")
    void testAddNullMessage() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            this.groupChat.addMessage(null);
        }, "Adding null message should throw IllegalArgumentException");

        assertEquals("Message cannot be null.", exception.getMessage());
    }
}

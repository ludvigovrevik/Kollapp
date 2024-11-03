package core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a group chat containing a list of messages.
 */
public class GroupChat implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final List<Message> messages;
    
    /**
     * Constructs an empty GroupChat.
     */
    public GroupChat() {
        this.messages = new ArrayList<>();
    }
    
    /**
     * Returns an unmodifiable list of messages.
     *
     * @return a list of messages
     */
    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    /**
     * Adds a message to the group chat.
     *
     * @param message the message to add
     * @throws IllegalArgumentException if the message is null
     */
    public void addMessage(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null.");
        }
        messages.add(message);
    }
}

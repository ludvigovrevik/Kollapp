package core;

import java.util.ArrayList;
import java.util.List;

public class MessageLog {
    private List<MessageContent> messages;    
    

    public MessageLog() {
        this.messages = new ArrayList<>();
    }

    
    public MessageLog(List<MessageContent> messages) {
        if (messages == null) {
            throw new IllegalArgumentException("Messages cannot be null.");
        }    
        this.messages = this.getMessages();
    }

    public List<MessageContent> getMessages() {
        return new ArrayList<>(messages);
    }

    public void addMessage(MessageContent message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null.");
        }
        messages.add(message);
    }
}

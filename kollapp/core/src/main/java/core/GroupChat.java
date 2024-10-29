package core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupChat implements Serializable{
    private List<Message> messages;    
    
    public GroupChat() {
        this.messages = new ArrayList<>();
    }

    public GroupChat(GroupChat messages) {
        this.messages = new ArrayList<>(messages.messages);
    }

    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    public void addMessage(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null.");
        }
        messages.add(message);
    }
}

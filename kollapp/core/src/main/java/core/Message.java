package core;

import java.util.ArrayList;
import java.util.List;

public class Message {
    private String author;
    private List<List<String>> messages = new ArrayList<>();

    public Message() {
    }
    
    public Message(String author, String text) {
        this.author = author;

        List<String> message = new ArrayList<>();
        message.add(author);
        message.add(text);
        messages.add(message);
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null");
        }
        this.author = author;
    }

    public void addMessage(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        List<String> localMessage = new ArrayList<>();
        localMessage.add(author);
        localMessage.add(text);
        messages.add(localMessage);
    }

    public List<List<String>> getMessages() {
        return new ArrayList<>(messages);
    }
}

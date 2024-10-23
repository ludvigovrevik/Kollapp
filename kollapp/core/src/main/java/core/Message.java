package core;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
    private String author;
    private String text;
    private LocalDateTime timestamp;

    // Default constructor for Jackson
    public Message() {
    }

    // Constructor with parameters
    public Message(String author, String text) {
        this.author = author;
        this.text = text;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and setters for Jackson
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null.");
        }
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null.");
        }
        this.text = text;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

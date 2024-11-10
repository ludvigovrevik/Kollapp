package core;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a message in the group chat.
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private String author;
    private String text;
    private LocalDateTime timestamp;

    /**
     * Default constructor for Jackson.
     */
    public Message() {
    }

    /**
     * Constructs a message with the specified author and text.
     *
     * @param author the author of the message
     * @param text   the text of the message
     */
    public Message(String author, String text) {
        this.author = author;
        this.text = text;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Returns the author of the message.
     * 
     * @return the author of the message
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author of the message.
     * 
     * @param author the author of the message
     * @throws IllegalArgumentException if the author is null
     */
    public void setAuthor(String author) {
        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null.");
        }
        this.author = author;
    }

    /**
     * Returns the text of the message.
     * 
     * @return the text of the message
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text of the message.
     * 
     * @param text the text of the message
     * @throws IllegalArgumentException if the text is null
     */
    public void setText(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null.");
        }
        this.text = text;
    }

    /**
     * Returns the timestamp of the message.
     * 
     * @return the timestamp of the message
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        return Objects.equals(author, message.author) &&
                Objects.equals(text, message.text) &&
                Objects.equals(timestamp, message.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, text, timestamp);
    }
}
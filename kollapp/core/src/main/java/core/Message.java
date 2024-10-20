package core;

public class Message {
    private String username;
    private String text;

    public Message() {
    }
    
    public Message(String username, String text) {
        this.username = username;
        this.text = text;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (this.username.equals(username)) {
            throw new IllegalArgumentException("Username cannot be identical to the current username");
        }
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        this.text = text;
    }
}

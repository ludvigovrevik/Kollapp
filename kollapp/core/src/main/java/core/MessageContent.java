package core;

public class MessageContent {
    private String author = "";
    private String text = "";

    public MessageContent() {
    }

    public MessageContent(String author, String text) {
        this.author = author;
        this.text = text;
    }

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
}

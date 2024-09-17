package app;

public class Task {
    private String description;
    private boolean isCompleted;

    // Constructor
    public Task(String description) {
        this.description = description;
        this.isCompleted = false;
    }

    // Getters and Setters
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    @Override
    public String toString() {
        return "• " + description;
    }
}

package app;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class Task {
    private String description;
    private boolean isCompleted;
    private LocalDate dateTime;

    // Constructor
    public Task(String description, LocalDate dateTime) {
        this.description = description.trim();
        this.dateTime = dateTime;
        this.isCompleted = false;
    }

    public Task(String description) {
        this.description = description;
        this.isCompleted = false;
    }

    // Getters and Setters
    public String getDescription() {
        return description;
    }

    public LocalDate getDateTime() {
        return dateTime;
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
        if (dateTime == null) {
            return "• " + description; 
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        return "• " + description + "   " + dateTime.format(formatter);
    }
}

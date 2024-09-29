package core;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task in the to-do list.
 */
public class Task {
    private int index;
    private String description;
    private boolean isCompleted;
    private LocalDate dateTime;

    // (required by Jackson)
    public Task() {
    }

    /**
     * Constructs a new Task with the specified description and date.
     * 
     * @param description The description of the task.
     * @param dateTime The date specified with the task.
     */
    public Task(String description, LocalDate dateTime) {
        this.description = description.trim();
        this.dateTime = dateTime;
        this.isCompleted = false;
    }

    public Task(String description) {
        this.description = description;
        this.isCompleted = false;
    }

    public String getDescription() {
        return description;
    }

    public int getIndex() {
        return index;
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
        // If the date is not set, return the description only
        if (dateTime == null) {
            return description; 
        }
        
        // Format the date in MMM dd, yyyy format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        return description + "   " + dateTime.format(formatter);
    }
}

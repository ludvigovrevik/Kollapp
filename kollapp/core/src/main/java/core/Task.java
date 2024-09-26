package core;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task in the to-do list.
 */
public class Task {
    private String description;
    private boolean isCompleted;
    private LocalDate dateTime;

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

    /**
     * Constructs a new Task with the specified description.
     * 
     * @param description The description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isCompleted = false;
    }

    /**
     * Gets the description of the task.
     * 
     * @return The description of the task.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the date of the task.
     * 
     * @return The date of the task.
     */
    public LocalDate getDateTime() {
        return dateTime;
    }

    /**
     * Sets the description of the task.
     * @param description The description of the task.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the isCompleted status of the task.
     * 
     * @return The isCompleted status of the task.
     */
    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * Sets the isCompleted status of the task.
     * 
     * @param completed The isCompleted status of the task.
     */
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    /**
     * Creates a textual representation of the task.
     * 
     *  return text representation of the task with the date if it exists.
     */
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

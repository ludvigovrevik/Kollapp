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
    private String taskDescription;
    private String taskPriority;

    // (required by Jackson)
    public Task() {
    }

    /**
     * Constructs a new Task with the specified description and date.
     * 
     * @param description The description of the task.
     * @param dateTime The date specified with the task.
     */
    public Task(String description, LocalDate dateTime, String taskDescription, String taskPriority) {
        this.description = description.trim();
        this.dateTime = dateTime;
        this.isCompleted = false;
        this.taskDescription = taskDescription;
        this.taskPriority = taskPriority;
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

    public String getTaskDescription() {
        return taskDescription;
    }

    public String getTaskPriority() {
        return taskPriority;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public void setTaskPriority(String taskPriority) {
        this.taskPriority = taskPriority;
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

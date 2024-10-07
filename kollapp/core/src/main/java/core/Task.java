package core;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task in the to-do list.
 */
public class Task {
    private int index;
    private String taskName;
    private boolean isCompleted;
    private LocalDate dateTime;
    private String description;
    private String priority;

    // (required by Jackson)
    public Task() {
    }

    /**
     * Constructs a new Task with the specified description and date.
     * 
     * @param description The description of the task.
     * @param dateTime The date specified with the task.
     */
    public Task(String taskName, LocalDate dateTime, String description, String priority) {
        this.taskName = taskName.trim();
        this.dateTime = dateTime;
        this.isCompleted = false;
        this.description = description;
        this.priority = priority;
    }

    public Task(String taskName) {
        this.taskName = taskName;
        this.isCompleted = false;
    }

    public String getTaskName() {
        return taskName;
    }

    public int getIndex() {
        return index;
    }

    public LocalDate getDateTime() {
        return dateTime;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getDescription() {
        return description;
    }

    public String getPriority() {
        return priority;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        // If the date is not set, return the description only
        if (dateTime == null) {
            return taskName; 
        }
        
        // Format the date in MMM dd, yyyy format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        return taskName + "   " + dateTime.format(formatter);
    }
}

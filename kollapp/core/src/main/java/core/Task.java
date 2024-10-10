package core;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * Represents a task in the to-do list.
 */
public class Task implements Serializable {
    private static final long serialVersionUID = 1L;

    private String taskName;
    private boolean isCompleted;
    private LocalDate dateTime;
    private String description;
    private String priority;
    public static final List<String> PRIORITY_NAMES = List.of("High", "Medium", "Low");

    // (required by Jackson)
    public Task() {
    }


    /**
     * Constructs a new Task by copying the properties of an existing Task.
     *
     * @param task the Task to copy
     */
    public Task(Task task) {
        this.taskName = task.taskName;
        this.isCompleted = task.isCompleted;
        this.dateTime = task.dateTime;
        this.description = task.description;
        this.priority = task.priority;
    }

    public Task(String taskName, LocalDate dateTime, String description, String priority) {
        taskName = taskName.trim();
        if (taskName.isBlank()) {
            throw new IllegalArgumentException("Task name cannot be empty.");
        }

        // Check if the priority level is valid
        if (priority != null && !PRIORITY_NAMES.contains(priority)) {
            throw new IllegalArgumentException("Invalid priority level.");
        }
        this.taskName = taskName.trim();
        this.dateTime = dateTime;
        this.isCompleted = false;
        this.description = description;
        this.priority = priority;
    }

    public Task(String taskName) {
        taskName = taskName.trim();
        if (taskName.isBlank()) {
            throw new IllegalArgumentException("Task name cannot be empty.");
        }
        this.taskName = taskName;
        this.isCompleted = false;
    }

    // Returns the task name
    public String getTaskName() {
        return taskName;
    }
    // Returns the task name
    public LocalDate getDateTime() {
        return dateTime;
    }

    // Sets the task name
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    // Returns the completion status of the task
    public boolean isCompleted() {
        return isCompleted;
    }

    // Sets the completion status of the task
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    // Returns the description of the task
    public String getDescription() {
        return description;
    }

    // Returns the priority of the task
    public String getPriority() {
        return priority;
    }

    // Sets the description of the task
    public void setDescription(String description) {
        this.description = description;
    }

    // Sets the priority of the task
    public void setPriority(String priority) {
        this.priority = priority;
    }
}

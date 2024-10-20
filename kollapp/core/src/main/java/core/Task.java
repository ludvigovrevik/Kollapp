package core;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * Represents a task in the to-do list, containing properties such as name, 
 * completion status, date, description, and priority.
 */
public class Task implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String taskName;
    private boolean isCompleted;
    private LocalDate dateTime;
    private String description;
    private String priority;
    public static final List<String> PRIORITY_NAMES = List.of("High", "Medium", "Low");

    // (Required by Jackson for deserialization)
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

    /**
     * Constructs a new Task with the specified properties.
     *
     * @param taskName the name of the task
     * @param dateTime the date and time associated with the task
     * @param description a description of the task
     * @param priority the priority level of the task (High, Medium, or Low)
     * @throws IllegalArgumentException if taskName is blank or priority is invalid
     */
    public Task(String taskName, LocalDate dateTime, String description, String priority) {
        if (taskName.isBlank()) {
            throw new IllegalArgumentException("Task name cannot be empty.");
        }

        if (priority != null && !PRIORITY_NAMES.contains(priority)) {
            throw new IllegalArgumentException("Invalid priority level.");
        }
        this.taskName = taskName.trim();
        this.dateTime = dateTime;
        this.isCompleted = false;
        this.description = description;
        this.priority = priority;
    }

    /**
     * Constructs a new Task with the specified name and default settings.
     *
     * @param taskName the name of the task
     * @throws IllegalArgumentException if taskName is blank
     */
    public Task(String taskName) {
        if (taskName.isBlank()) {
            throw new IllegalArgumentException("Task name cannot be empty.");
        }
        this.taskName = taskName.trim();
        this.isCompleted = false;
    }

    /**
     * Returns the task name.
     *
     * @return the name of the task
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * Sets the task name.
     *
     * @param taskName the name to set for the task
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    /**
     * Returns the date and time associated with the task.
     *
     * @return the task's date and time
     */
    public LocalDate getDateTime() {
        return dateTime;
    }

    /**
     * Returns the completion status of the task.
     *
     * @return true if the task is completed, false otherwise
     */
    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * Sets the completion status of the task.
     *
     * @param completed true if the task is completed, false otherwise
     */
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    /**
     * Returns the description of the task.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the task.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the priority of the task.
     *
     * @return the task priority
     */
    public String getPriority() {
        return priority;
    }

    /**
     * Sets the priority of the task.
     *
     * @param priority the priority to set (High, Medium, or Low)
     */
    public void setPriority(String priority) {
        this.priority = priority;
    }
}

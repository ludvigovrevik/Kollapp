package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a to-do list that contains tasks.
 */
public class ToDoList implements java.io.Serializable {
    private List<Task> tasks = new ArrayList<>();

    public ToDoList() { }

    // Add a task to the to-do list
    public void addTask(Task newTask) {
        if (newTask == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }
        tasks.add(newTask);
    }

    // Get the list of tasks
    public List<Task> getTasks() {
        return tasks;
    }

    // Remove a task from the to-do list
    public void removeTask(Task task) {
        if (!tasks.contains(task)) {
            throw new IndexOutOfBoundsException("Index is out of range.");
        }
        tasks.remove(task);
    }
}

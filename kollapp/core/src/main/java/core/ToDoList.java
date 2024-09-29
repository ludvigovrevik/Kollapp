package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a to-do list that contains tasks.
 */
public class ToDoList implements java.io.Serializable {
    private List<Task> tasks = new ArrayList<>();

    // required by Jackson
    public ToDoList() {}

    public void addTask(Task newTask) {
        if (newTask == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }
        tasks.add(newTask);
    }

    public void removeTask(int index) {
        if (index < 0 || index >= tasks.size()) {
            throw new IndexOutOfBoundsException("Index is out of range.");
        }
        tasks.remove(index);
    }

    public void removeTask(Task task) {
        if (!tasks.contains(task)) {
            throw new IndexOutOfBoundsException("Index is out of range.");
        }
        tasks.remove(task);
    }

    public void updateTask(int index, Task updatedTask) {
        if (index < 0 || index >= tasks.size()) {
            throw new IndexOutOfBoundsException("Index is out of range.");
        }
        tasks.set(index, updatedTask);
    }

    public List<Task> getTasks() {
        return tasks;
    }
}

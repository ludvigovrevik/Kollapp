package app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ToDoList {

    private ObservableList<Task> tasks;

    // Constructor
    public ToDoList() {
        tasks = FXCollections.observableArrayList();
    }

    /**
     * Adds a new task to the to-do list.
     *
     * @param newTask The task to be added.
     * @throws IllegalArgumentException if the task is null.
     */
    public void addTask(Task newTask) {
        if (newTask == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }
        tasks.add(newTask);
    }

    /**
     * Removes a task from the to-do list.
     *
     * @param index The index of the task to be removed.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    public void removeTask(int index) {
        if (index < 0 || index >= tasks.size()) {
            throw new IndexOutOfBoundsException("Index is out of range.");
        }
        tasks.remove(index);
    }

    /**
     * Updates a task in the to-do list.
     *
     * @param index       The index of the task to be updated.
     * @param updatedTask The updated task.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    public void updateTask(int index, Task updatedTask) {
        if (index < 0 || index >= tasks.size()) {
            throw new IndexOutOfBoundsException("Index is out of range.");
        }
        tasks.set(index, updatedTask);
    }

    /**
     * Returns an unmodifiable list of tasks in the to-do list.
     *
     * @return An unmodifiable list of tasks.
     */
    public ObservableList<Task> getTasks() {
        return tasks; // You might return an unmodifiable list depending on your needs
    }
}

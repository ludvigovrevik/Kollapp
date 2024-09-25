package app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Scanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Represents a to-do list that contains tasks.
 */
public class ToDoList {

    private ObservableList<Task> tasks;
    private static final String SAVE_STATE_FILE = "save_state.txt";

    /**
     * Constructs a new to-do list and loads tasks from a file.
     */
    public ToDoList() {
        tasks = FXCollections.observableArrayList();
        loadTasksFromFile();
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
        saveTasksToFile();
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
        saveTasksToFile();
    }
    public void removeTask(Task task) {
        if (!tasks.contains(task)) {
            throw new IndexOutOfBoundsException("Index is out of range.");
        }
        tasks.remove(task);
        saveTasksToFile();
    }

    /**
     * Saves the tasks in the to-do list to a file.
     */
    public void saveTasksToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_STATE_FILE))) {
            for (Task task : tasks) {
                writer.write(task.getDescription() + "|" + (task.getDateTime() != null ? task.getDateTime().toString() : "null"));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads tasks from a file into the to-do list.
     */
    private void loadTasksFromFile() {
        try (Scanner scanner = new Scanner(new File(SAVE_STATE_FILE))){
            // Read each line from the file and create a task object
            while (scanner.hasNext()) {
                String line  = scanner.nextLine();
                String[] parts = line.split("\\|");

                String taskDescription = parts[0];

                LocalDate dateTime = "null".equals(parts[1]) ? null : LocalDate.parse(parts[1]);

                Task task = new Task(taskDescription, dateTime);
                tasks.add(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        // Return an unmodifiable list to prevent external modification
        return tasks;
    }
}

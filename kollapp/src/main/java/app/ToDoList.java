package app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Scanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ToDoList {

    private ObservableList<Task> tasks;
    private static final String SAVE_STATE_FILE = "save_state.txt";

    // Constructor
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

    private void saveTasksToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_STATE_FILE))) {
            for (Task task : tasks) {
                writer.write(task.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTasksFromFile() {
        try (Scanner scanner = new Scanner(new File(SAVE_STATE_FILE))){
            while (scanner.hasNext()) {
                String taskDescription = scanner.nextLine();
                LocalDate dateTime = LocalDate.of(2024, 9, 18);
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
        return tasks; // You might return an unmodifiable list depending on your needs
    }
}

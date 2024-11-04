package core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents a to-do list that contains multiple tasks.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ToDoList implements Serializable {
    private List<Task> tasks = new ArrayList<>();

    /**
     * Default constructor for creating an empty ToDoList.
     */
    public ToDoList() {
    }

    /**
     * Constructs a new ToDoList by copying the tasks from another ToDoList.
     *
     * @param toDoList the ToDoList to copy
     */
    public ToDoList(ToDoList toDoList) {
        for (Task task : toDoList.getTasks()) {
            tasks.add(new Task(task));
        }
    }

    /**
     * Adds a task to the to-do list.
     *
     * @param newTask the task to add
     * @throws IllegalArgumentException if the task is null
     */
    public void addTask(Task newTask) {
        if (newTask == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }
        tasks.add(newTask);
    }

    /**
     * Returns a copy of the list of tasks.
     *
     * @return a list of tasks
     */
    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    /**
     * Removes a task from the to-do list.
     *
     * @param task the task to remove
     * @throws IndexOutOfBoundsException if the task is not in the list
     */
    public void removeTask(Task task) {
        if (!tasks.contains(task)) {
            throw new IndexOutOfBoundsException("Task is not in the list.");
        }
        tasks.remove(task);
    }
}

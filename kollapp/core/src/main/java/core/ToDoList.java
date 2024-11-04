package core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a to-do list that contains multiple tasks and expenses.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ToDoList implements Serializable {
    @JsonProperty
    private List<Task> tasks = new ArrayList<>();
    @JsonProperty
    private List<Expense> expenses = new ArrayList<>();

    /**
     * Default constructor for creating an empty ToDoList.
     */
    public ToDoList() {
    }

    /**
     * Constructs a new ToDoList by copying tasks and expenses from another ToDoList.
     *
     * @param toDoList the ToDoList to copy.
     */
    public ToDoList(ToDoList toDoList) {
        for (Task task : toDoList.getTasks()) {
            tasks.add(new Task(task));
        }
        for (Expense expense : toDoList.getExpenses()) {
            expenses.add(new Expense(expense));
        }
    }

    // Task methods remain unchanged

    public void addTask(Task newTask) {
        if (newTask == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }
        tasks.add(newTask);
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    public void removeTask(Task task) {
        if (!tasks.contains(task)) {
            throw new IndexOutOfBoundsException("Task is not in the list.");
        }
        tasks.remove(task);
    }

    // New methods for handling expenses

    /**
     * Adds an expense to the to-do list.
     *
     * @param newExpense the expense to add.
     * @throws IllegalArgumentException if the expense is null.
     */
    public void addExpense(Expense newExpense) {
        if (newExpense == null) {
            throw new IllegalArgumentException("Expense cannot be null.");
        }
        expenses.add(newExpense);
    }

    /**
     * Returns a copy of the list of expenses.
     *
     * @return a list of expenses.
     */
    public List<Expense> getExpenses() {
        return new ArrayList<>(expenses);
    }

    /**
     * Removes an expense from the to-do list.
     *
     * @param expense the expense to remove.
     * @throws IndexOutOfBoundsException if the expense is not in the list.
     */
    public void removeExpense(Expense expense) {
        if (!expenses.contains(expense)) {
            throw new IndexOutOfBoundsException("Expense is not in the list.");
        }
        expenses.remove(expense);
    }
}

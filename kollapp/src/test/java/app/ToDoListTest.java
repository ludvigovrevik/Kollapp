package app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;

class ToDoListTest {

    private ToDoList toDoList;
    private Task task;

    @BeforeEach
    void setUp() {
        toDoList = new ToDoList();
        task = new Task("Test Task");
    }

    @Test
    void testConstructorInitializesEmptyList() {
        assertNotNull(toDoList.getTasks(), "Tasks list should not be null after construction");
    }

    @Test
    void testAddTaskAddsTaskSuccessfully() {
        toDoList.addTask(task);

        assertEquals(true, toDoList.getTasks().contains(task), "Tasks list should contain the added task");

        toDoList.removeTask(toDoList.getTasks().size() - 1);
    }

    @Test
    void testAddTaskThrowsExceptionWhenTaskIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            toDoList.addTask(null);
        }, "Exception should be thrown when task is null");
    }

    @Test
    void testRemoveTaskRemovesTaskSuccessfully() {
        toDoList.addTask(task);
        toDoList.removeTask(toDoList.getTasks().size() - 1);

        assertEquals(true, !toDoList.getTasks().contains(task), "Tasks list should not contain the task after removing the task");
    }

    @Test
    void testRemoveTaskThrowsExceptionWhenIndexIsOutOfBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            toDoList.removeTask(-1);
        }, "Exception should be thrown when index is out of bounds");
        assertThrows(IndexOutOfBoundsException.class, () -> {
            toDoList.removeTask(toDoList.getTasks().size());
        }, "Exception should be thrown when index is out of bounds");
    }

    @Test
    void testUpdateTaskUpdatesTaskSuccessfully() {
        Task originalTask = new Task("Original Task");
        Task updatedTask = new Task("Updated Task");
        toDoList.addTask(originalTask);
        toDoList.updateTask(toDoList.getTasks().size() - 1, updatedTask);

        assertEquals(false, toDoList.getTasks().contains(originalTask), "The task should be updated to the new task");
        assertEquals(true, toDoList.getTasks().contains(updatedTask), "The task should be updated to the new task");

        toDoList.removeTask(toDoList.getTasks().size() - 1);
    }

    @Test
    void testGetTasksModificationAffectsOriginalList() {
        Task task = new Task("Task");
        toDoList.addTask(task);

        // Directly modify the list obtained from getTasks()
        toDoList.getTasks().remove(task);

        assertEquals(false, toDoList.getTasks().contains(task), "Modifying the returned list should affect the original list");
    }
}

package app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

class ToDoListTest {

    private ToDoList toDoList;
    private Task task;

    //filler-date

    @BeforeEach
    void setUp() {
        
        toDoList = new ToDoList();
        task = new Task("Test Task");
    }

    @Test
    void testConstructorInitializesEmptyList() {
        assertNotNull(toDoList.getTasks(), "Tasks list should not be null after construction");
        assertEquals(0, toDoList.getTasks().size(), "Tasks list should be empty upon initialization");
    }

    @Test
    void testAddTaskAddsTaskSuccessfully() {
        toDoList.addTask(task);

        assertEquals(1, toDoList.getTasks().size(), "Tasks list should contain one task after adding");
        assertEquals(task, toDoList.getTasks().get(0), "The task added should be the one retrieved");
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
        toDoList.removeTask(0);

        assertEquals(0, toDoList.getTasks().size(), "Tasks list should be empty after removing the task");
    }

    @Test
    void testRemoveTaskThrowsExceptionWhenIndexIsOutOfBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            toDoList.removeTask(0);
        }, "Exception should be thrown when index is out of bounds");
    }

    @Test
    void testUpdateTaskUpdatesTaskSuccessfully() {
        Task originalTask = new Task("Original Task");
        Task updatedTask = new Task("Updated Task");
        toDoList.addTask(originalTask);
        toDoList.updateTask(0, updatedTask);

        assertEquals(1, toDoList.getTasks().size(), "Tasks list should still contain one task after update");
        assertEquals(updatedTask, toDoList.getTasks().get(0), "The task should be updated to the new task");
    }

    @Test
    void testGetTasksReturnsTasksList() {
        Task task1 = new Task("Task 1");
        Task task2 = new Task("Task 2");
        toDoList.addTask(task1);
        toDoList.addTask(task2);

        assertEquals(2, toDoList.getTasks().size(), "Tasks list should contain two tasks");
        assertTrue(toDoList.getTasks().contains(task1), "Tasks list should contain Task 1");
        assertTrue(toDoList.getTasks().contains(task2), "Tasks list should contain Task 2");
    }

    @Test
    void testGetTasksModificationAffectsOriginalList() {
        Task task = new Task("Task");
        toDoList.addTask(task);

        // Directly modify the list obtained from getTasks()
        toDoList.getTasks().remove(task);

        assertEquals(0, toDoList.getTasks().size(), "Modifying the returned list should affect the original list");
    }
}

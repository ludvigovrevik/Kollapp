package core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.util.List;

class ToDoListTest {

    private ToDoList toDoList;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        toDoList = new ToDoList();
        task1 = new Task("Test task1");
        task2 = new Task("Test task2");
        task3 = new Task("Test task3");
    }

    @Test
    void testConstructorInitializesEmptyList() {
        assertNotNull(toDoList.getTasks(), "Tasks list should not be null after construction");
    }

    @Test
    public void testAddTask() {
        toDoList.addTask(task1);
        toDoList.addTask(task2);
        toDoList.addTask(task3);
        
        // throws IllegalArgument when adding task4
        assertThrows(IllegalArgumentException.class, () -> {
            toDoList.addTask(null);
        });

        List<Task> tasks = toDoList.getTasks();
        assertEquals(3, tasks.size()); // Verify that the toDoList has 3 tasks
        assertEquals(task1, tasks.get(0)); // Verify that the added task1 is in the toDoList
        assertEquals(task2, tasks.get(1)); // Verify that the added task2 is in the toDoList
        assertEquals(task3, tasks.get(2)); // Verify that the added task3 is in the toDoList
    }

    @Test
    public void testRemoveTask() {
        toDoList.addTask(task1);
        toDoList.addTask(task2);
        toDoList.addTask(task3);
        
        // throws IndexOutOfBoundsException when removing task4
        assertThrows(IndexOutOfBoundsException.class, () -> {
            toDoList.removeTask(mock(Task.class));
        });

        toDoList.removeTask(task2);
        List<Task> tasks = toDoList.getTasks();
        assertEquals(2, tasks.size()); // Verify that the toDoList has 2 tasks
        assertEquals(task1, tasks.get(0)); // Verify that the task1 is in the toDoList
        assertEquals(task3, tasks.get(1)); // Verify that the task3 is in the toDoList
    }

    @Test
    public void testGetTasks() {
        toDoList.addTask(task1);
        toDoList.addTask(task2);
        toDoList.addTask(task3);
        List<Task> tasks = toDoList.getTasks();
        assertEquals(3, tasks.size()); // Verify that the toDoList has 3 tasks
        assertEquals(task1, tasks.get(0)); // Verify that the task1 is in the toDoList
        assertEquals(task2, tasks.get(1)); // Verify that the task2 is in the toDoList
        assertEquals(task3, tasks.get(2)); // Verify that the task3 is in the toDoList
    }
}

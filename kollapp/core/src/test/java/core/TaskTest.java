package core;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Task} class.
 */
@Tag("task")
class TaskTest {

    private Task task1;
    private Task task2;
    private static final String TASK_NAME = "Buy groceries";
    private static final String DESCRIPTION = "Get milk and eggs from the store";
    private static final String PRIORITY = Task.PRIORITY_NAMES.get(0); // High
    private static final LocalDate DUE_DATE = LocalDate.of(2024, 10, 7);  // Example date: Oct 7, 2024
    
    @BeforeEach
    public void setUp() {
        this.task1 = new Task(TASK_NAME);  // Initialize your object
        this.task2 = new Task(TASK_NAME, DUE_DATE, DESCRIPTION, PRIORITY);
    }
    
    @Test
    @DisplayName("Test task initialization with default values")
    @Tag("init")
    void testTaskInitialization() {
        assertNotNull(new Task(), "Task object should not be null");
        Assertions.assertEquals(TASK_NAME, task1.getTaskName(), "Description should match the input");
        Assertions.assertFalse(task1.isCompleted(), "New task should not be completed");
        Assertions.assertNull(task1.getDescription(), "Description should be null");
        Assertions.assertNull(task1.getPriority(), "Priority should be null");
        Assertions.assertNull(task1.getDateTime(), "Date should be null");
    }

    @Test
    @DisplayName("Test task constructor with valid and invalid inputs")
    @Tag("constructor")
    public void testTaskConstructor() {
        assertEquals(TASK_NAME, task2.getTaskName(), "Task name should be 'Buy groceries'");  // Check if taskName is trimmed
        assertEquals(DUE_DATE, task2.getDateTime(), "Due date should be Oct 7, 2024");
        assertEquals(DESCRIPTION, task2.getDescription(), "Description should be 'Get milk and eggs from the store'");
        assertEquals(PRIORITY, task2.getPriority(), "Priority should be 'High'");
        assertFalse(task2.isCompleted());  // Check if isCompleted is set to false by default
        
        assertThrows(IllegalArgumentException.class, () -> new Task(""), "Task name cannot be empty");
        assertThrows(IllegalArgumentException.class, () -> new Task(" ", DUE_DATE, DESCRIPTION, PRIORITY), "Task name cannot be blank");
        assertThrows(IllegalArgumentException.class, () -> new Task(TASK_NAME, DUE_DATE, DESCRIPTION, "Invalid Priority"), "Invalid priority should throw exception");
        
        Task task = new Task(TASK_NAME, DUE_DATE, DESCRIPTION, null);
        assertNull(task.getPriority(), "Null priority should be allowed");
    }

    @Test
    @DisplayName("Test getting task name")
    @Tag("getter")
    void testGetTaskName() {
        Assertions.assertEquals(TASK_NAME, task1.getTaskName(), "Task name should match the input");
        Assertions.assertEquals(TASK_NAME, task2.getTaskName(), "Task name should match the input");   
    }

    @Test
    @DisplayName("Test getting and setting task priority")
    @Tag("getter")
    @Tag("setter")
    void testGetPriority() {
        Assertions.assertNull(task1.getPriority(), "Priority should be null");
        Assertions.assertEquals(PRIORITY, task2.getPriority(), "Priority should match the input");
    }

    @Test
    @DisplayName("Test setting task priority")
    @Tag("setter")
    void testSetPriority() {
        Assertions.assertNull(task1.getPriority(), "Priority should be null");
        Assertions.assertEquals(PRIORITY, task2.getPriority(), "Priority should match the input");
        String newPriority = Task.PRIORITY_NAMES.get(2);
        this.task1.setPriority(newPriority);
        Assertions.assertEquals(newPriority, task1.getPriority(), "Priority should be updated");
    }

    @Test
    @DisplayName("Test setting task name")
    @Tag("setter")
    void testSetTaskName() {
        String newTaskName = "Buy groceries and cook dinner";
        this.task1.setTaskName(newTaskName);
        Assertions.assertEquals(newTaskName, task1.getTaskName(), "Task name should be updated");
    }

    @Test
    @DisplayName("Test setting task description")
    @Tag("setter")
    void testSetDescription() {
        String newDescription = "Updated Description";
        this.task1.setDescription(newDescription);
        Assertions.assertEquals(newDescription, task1.getDescription(), "Description should be updated");
        
        String newDescription2 = "Updated Description 2";
        this.task2.setDescription(newDescription2);
        Assertions.assertEquals(newDescription2, task2.getDescription(), "Description should be updated");
    }

    @Test
    @DisplayName("Test getting task due date")
    @Tag("getter")
    void testGetDateTime() {
        Assertions.assertNull(task1.getDateTime(), "Date should be null");
        Assertions.assertEquals(DUE_DATE, task2.getDateTime(), "Date should match the input");
    }

    @Test
    @DisplayName("Test getting task description")
    @Tag("getter")
    void testGetDescription() {
        Assertions.assertNull(task1.getDescription(), "Description should be null");
        Assertions.assertEquals(DESCRIPTION, task2.getDescription(), "Description should match the input");
    }

    @Test
    @DisplayName("Test if task is completed")
    @Tag("getter")
    void testIsCompleted() {
        Assertions.assertFalse(task1.isCompleted(), "Task should not be completed");
        Assertions.assertFalse(task2.isCompleted(), "Task should not be completed");
    }

    @Test
    @DisplayName("Test setting task completion status")
    @Tag("setter")
    void testSetCompleted() {
        task1.setCompleted(true);
        Assertions.assertTrue(task1.isCompleted(), "Task should be completed");
        
        task2.setCompleted(true);
        Assertions.assertTrue(task2.isCompleted(), "Task should be completed");

        task1.setCompleted(false);
        Assertions.assertFalse(task1.isCompleted(), "Task should not be completed");

        task2.setCompleted(false);
        Assertions.assertFalse(task2.isCompleted(), "Task should not be completed");
    }
}

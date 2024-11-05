package core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.util.List;

/**
 * Unit tests for the {@link ToDoList} class.
 */
@Tag("todolist")
class ToDoListTest {

    private ToDoList toDoList;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    @DisplayName("Initialize test setup for ToDoList and tasks")
    void setUp() {
        toDoList = new ToDoList();
        task1 = new Task("Test task1");
        task2 = new Task("Test task2");
        task3 = new Task("Test task3");
    }

    @Test
    @DisplayName("Test ToDoList constructor initializes an empty task list")
    @Tag("constructor")
    void testConstructorInitializesEmptyList() {
        assertNotNull(toDoList.getTasks(), "Tasks list should not be null after construction");
    }

    @Test
    @DisplayName("Test ToDoList copy constructor")
    @Tag("copy")
    void testToDoListCopyConstructor() {
        ToDoList originalToDoList = new ToDoList();
        originalToDoList.addTask(new Task("Task 1", LocalDate.of(2023, 11, 5), "Description 1", "High"));
        originalToDoList.addTask(new Task("Task 2", LocalDate.of(2023, 11, 6), "Description 2", "Medium"));

        ToDoList copiedToDoList = new ToDoList(originalToDoList);

        List<Task> originalTasks = originalToDoList.getTasks();
        List<Task> copiedTasks = copiedToDoList.getTasks();

        assertNotNull(copiedToDoList, "Copied ToDoList should not be null");
        assertEquals(originalTasks.size(), copiedTasks.size(), "Copied list should have the same number of tasks as the original");

        for (int i = 0; i < originalTasks.size(); i++) {
            Task originalTask = originalTasks.get(i);
            Task copiedTask = copiedTasks.get(i);

            assertNotSame(originalTask, copiedTask, "Copied task should be a separate instance");
            assertEquals(originalTask.getTaskName(), copiedTask.getTaskName(), "Task names should match");
            assertEquals(originalTask.getDateTime(), copiedTask.getDateTime(), "Dates should match");
            assertEquals(originalTask.getDescription(), copiedTask.getDescription(), "Descriptions should match");
            assertEquals(originalTask.getPriority(), copiedTask.getPriority(), "Priorities should match");
            assertEquals(originalTask.isCompleted(), copiedTask.isCompleted(), "Completion status should match");
        }
    }


    @Test
    @DisplayName("Test adding tasks to the ToDoList")
    @Tag("add")
    public void testAddTask() {
        toDoList.addTask(task1);
        toDoList.addTask(task2);
        toDoList.addTask(task3);
        
        assertThrows(IllegalArgumentException.class, () -> {
            toDoList.addTask(null);
        }, "Adding a null task should throw IllegalArgumentException");

        List<Task> tasks = toDoList.getTasks();
        assertEquals(3, tasks.size(), "ToDoList should contain 3 tasks");
        assertEquals(task1, tasks.get(0), "First task should be task1");
        assertEquals(task2, tasks.get(1), "Second task should be task2");
        assertEquals(task3, tasks.get(2), "Third task should be task3");
    }

    @Test
    @DisplayName("Test removing tasks from the ToDoList")
    @Tag("remove")
    public void testRemoveTask() {
        toDoList.addTask(task1);
        toDoList.addTask(task2);
        toDoList.addTask(task3);
        
        assertThrows(IndexOutOfBoundsException.class, () -> {
            toDoList.removeTask(mock(Task.class));
        }, "Removing a task not in the list should throw IndexOutOfBoundsException");

        toDoList.removeTask(task2);
        List<Task> tasks = toDoList.getTasks();
        assertEquals(2, tasks.size(), "ToDoList should contain 2 tasks after removal");
        assertEquals(task1, tasks.get(0), "First task should be task1");
        assertEquals(task3, tasks.get(1), "Second task should be task3");
    }

    @Test
    @DisplayName("Test retrieving tasks from the ToDoList")
    @Tag("getter")
    public void testGetTasks() {
        toDoList.addTask(task1);
        toDoList.addTask(task2);
        toDoList.addTask(task3);
        List<Task> tasks = toDoList.getTasks();
        assertEquals(3, tasks.size(), "ToDoList should contain 3 tasks");
        assertEquals(task1, tasks.get(0), "First task should be task1");
        assertEquals(task2, tasks.get(1), "Second task should be task2");
        assertEquals(task3, tasks.get(2), "Third task should be task3");
    }
}

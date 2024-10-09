package persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Comparator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import core.Task;
import core.ToDoList;
import core.User;

public class ToDoListHandlerTest {

    private ToDoListHandler toDoListHandler;
    private User user;
    private Path testFolderPath;
    private Path groupTestFolderPath;

    @BeforeEach
    public void setUp() throws IOException {
        // Use a test-specific directory to avoid interfering with real data
        this.testFolderPath = Paths.get("src", "main", "java", "persistence", "todolists", "tests");
        this.groupTestFolderPath = Paths.get("src", "main", "java", "persistence", "grouptodolists", "tests");
        // Initialize ToDoListHandler with the test path
        toDoListHandler = new ToDoListHandler(testFolderPath.toString() + File.separator, groupTestFolderPath.toString() + File.separator);

        user = new User("testUser", "password123");

        // Create the test directory if it doesn't exist
        if (!Files.exists(testFolderPath)) {
            Files.createDirectories(testFolderPath);
        }
    }

    @AfterEach
    public void tearDown() throws IOException {
        // Delete the content in the test directory
        if (Files.exists(testFolderPath)) {
            Files.walk(testFolderPath)
                 .sorted(Comparator.reverseOrder())
                 .map(Path::toFile)
                 .forEach(File::delete);
        }
    }

    @Test
    public void testConstructors() throws Exception {
        // Create an instance using the default constructor
        ToDoListHandler defaultHandler = new ToDoListHandler();

        // Use reflection to access the private TODOLIST_PATH field
        Field pathField = ToDoListHandler.class.getDeclaredField("TODOLIST_PATH");
        pathField.setAccessible(true);
        String actualPath = (String) pathField.get(defaultHandler);

        // Expected default path
        String expectedPath = Paths.get("..", "persistence", "src", "main", "java", "persistence", "todolists")
                .toString() + File.separator;
        assertEquals(expectedPath, actualPath, "The default TODOLIST_PATH should be set correctly.");

        // Use reflection to access the private mapper field
        Field mapperField = ToDoListHandler.class.getDeclaredField("mapper");
        mapperField.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) mapperField.get(defaultHandler);
        assertNotNull(mapper, "The ObjectMapper should not be null.");

        // Verify that the JavaTimeModule is registered in the mapper
        LocalDate testDate = LocalDate.now();
        String dateJson = mapper.writeValueAsString(testDate);
        LocalDate parsedDate = mapper.readValue(dateJson, LocalDate.class);
        assertEquals(testDate, parsedDate, "The ObjectMapper should correctly serialize and deserialize LocalDate.");
    }

    @Test
    public void testAssignToDoList_CreatesEmptyFile() {
        // Call the method
        toDoListHandler.assignToDoList(user);

        // Verify that the file exists
        File expectedFile = new File(testFolderPath.toString(), user.getUsername() + ".json");
        assertTrue(expectedFile.exists(), "The to-do list file should be created.");

        // Verify that the file contains an empty ToDoList
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            ToDoList loadedList = mapper.readValue(expectedFile, ToDoList.class);
            assertNotNull(loadedList, "The loaded to-do list should not be null.");
            assertTrue(loadedList.getTasks().isEmpty(), "The to-do list should be empty.");
        } catch (IOException e) {
            fail("Failed to read the to-do list file.");
        }
    }

    @Test
    public void testLoadToDoList_FileExists() {
        // Assign an empty to-do list to create the file
        toDoListHandler.assignToDoList(user);

        // Prepare a ToDoList with tasks
        ToDoList toDoList = new ToDoList();
        Task task1 = new Task("Sample Task1");
        task1.setPriority("Medium");
        toDoList.addTask(task1);

        Task task2 = new Task("Sample Task2", LocalDate.now(), "Description", "High");
        toDoList.addTask(task2);

        Task task3 = new Task("Sample Task3");
        task3.setPriority("Low");
        task3.setCompleted(true);
        toDoList.addTask(task3);

        // Update the to-do list file with the new tasks
        toDoListHandler.updateToDoList(user, toDoList);

        // Load the to-do list using the handler
        ToDoList loadedList = toDoListHandler.loadToDoList(user);

        // Verify the contents
        assertNotNull(loadedList, "The loaded to-do list should not be null.");
        assertEquals(3, loadedList.getTasks().size(), "The to-do list should have three tasks.");
        assertEquals("Sample Task1", loadedList.getTasks().get(0).getTaskName(), "First task name should match.");
        assertEquals("Description", loadedList.getTasks().get(1).getDescription(), "Second task description should match.");
        assertEquals(true, loadedList.getTasks().get(2).isCompleted(), "Third task should be completed.");
    }

    @Test
    public void testLoadToDoList_FileDoesNotExist() {
        // Ensure the file does not exist
        File file = new File(testFolderPath.toString(), user.getUsername() + ".json");
        if (file.exists()) {
            file.delete();
        }

        // Attempt to load the to-do list and expect an exception
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            toDoListHandler.loadToDoList(user);
        });

        String expectedMessage = "To-do list file does not exist for user: " + user.getUsername();
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage, "Exception message should match.");
    }

    @Test
    public void testUpdateToDoList_FileDoesNotExist() {
        // Ensure the file does not exist
        File file = new File(testFolderPath.toString(), user.getUsername() + ".json");
        if (file.exists()) {
            file.delete();
        }

        // Create a to-do list and attempt to update
        ToDoList toDoList = new ToDoList();
        toDoList.addTask(new Task("Simple task"));

        // Update the to-do list file
        toDoListHandler.updateToDoList(user, toDoList);

        // Verify that the file now exists
        assertTrue(file.exists(), "The to-do list file should be created after update.");

        // Load the to-do list and verify contents
        ToDoList loadedList = toDoListHandler.loadToDoList(user);
        assertNotNull(loadedList, "The loaded to-do list should not be null.");
        assertEquals(1, loadedList.getTasks().size(), "The to-do list should have one task.");
        assertEquals("Simple task", loadedList.getTasks().get(0).getTaskName(), "Task name should match.");
    }

    @Test
    public void testAssignToDoList_OverwritesExistingFile() throws IOException {
        // Create an initial to-do list file
        ToDoList toDoList = new ToDoList();
        toDoList.addTask(new Task("Simple task"));
        toDoListHandler.updateToDoList(user, toDoList);

        // Assign a new empty to-do list
        toDoListHandler.assignToDoList(user);

        // Load the to-do list and verify it's empty
        ToDoList loadedList = toDoListHandler.loadToDoList(user);
        assertNotNull(loadedList, "The loaded to-do list should not be null.");
        assertTrue(loadedList.getTasks().isEmpty(), "The to-do list should be empty after assignment.");
    }

    @Test
    public void testUpdateToDoList_WithEmptyList() throws IOException {
        // Assign an empty to-do list
        toDoListHandler.assignToDoList(user);

        // Update with an empty to-do list
        ToDoList toDoList = new ToDoList();
        toDoListHandler.updateToDoList(user, toDoList);

        // Load and verify the to-do list is empty
        ToDoList loadedList = toDoListHandler.loadToDoList(user);
        assertNotNull(loadedList, "The loaded to-do list should not be null.");
        assertTrue(loadedList.getTasks().isEmpty(), "The to-do list should be empty.");
    }
}

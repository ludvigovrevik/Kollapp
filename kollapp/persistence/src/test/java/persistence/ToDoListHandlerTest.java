package persistence;

import static org.junit.jupiter.api.Assertions.*;

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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import core.Task;
import core.ToDoList;
import core.User;
import core.UserGroup;

/**
 * Unit tests for the {@link ToDoListHandler} class.
 */
@Tag("unit")
public class ToDoListHandlerTest {

    private User user;
    private UserGroup userGroup;
    
    private Path testToDoListFolderPath;
    private Path groupTestFolderPath;
    private Path userTestFolderPath;

    private ToDoListHandler toDoListHandler;
    private GroupHandler groupHandler;
    private UserHandler userHandler;

    /**
     * Sets up the test environment by creating directories and initializing handlers.
     */
    @BeforeEach
    public void setUp() throws IOException {
        this.testToDoListFolderPath = Paths.get("src", "main", "java", "persistence", "todolists", "tests");
        this.groupTestFolderPath = Paths.get("src", "main", "java", "persistence", "grouptodolists", "tests");
        this.userTestFolderPath = Paths.get("src", "main", "java", "persistence", "users", "tests");
        
        toDoListHandler = new ToDoListHandler(testToDoListFolderPath.toString() + File.separator, groupTestFolderPath.toString() + File.separator);
        userHandler = new UserHandler(userTestFolderPath.toString() + File.separator);
        groupHandler = new GroupHandler(groupTestFolderPath.toString() + File.separator, groupTestFolderPath.toString(), userHandler);
        
        user = new User("testUser", "password123");
        userGroup = new UserGroup("testGroup");

        if (!Files.exists(testToDoListFolderPath)) {
            Files.createDirectories(testToDoListFolderPath);
        }
        if (!Files.exists(groupTestFolderPath)) {
            Files.createDirectories(groupTestFolderPath);
        }
        if (!Files.exists(userTestFolderPath)) {
            Files.createDirectories(userTestFolderPath);
        }
    }

    /**
     * Cleans up test directories after each test.
     */
    @AfterEach
    public void tearDown() throws IOException {
        if (Files.exists(testToDoListFolderPath)) {
            Files.walk(testToDoListFolderPath).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
        if (Files.exists(groupTestFolderPath)) {
            Files.walk(groupTestFolderPath).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
        if (Files.exists(userTestFolderPath)) {
            Files.walk(userTestFolderPath).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
    }

    /**
     * Tests the default constructor of ToDoListHandler.
     * Ensures that the default paths and ObjectMapper are correctly initialized.
     */
    @Test
    @DisplayName("Test default constructor initialization")
    @Tag("constructor")
    public void testConstructors() throws Exception {
        ToDoListHandler defaultHandler = new ToDoListHandler();
    
        Field pathField = ToDoListHandler.class.getDeclaredField("toDoListPath");
        pathField.setAccessible(true);
        String actualPath = (String) pathField.get(defaultHandler);

        String expectedPath = Paths.get("..", "persistence", "src", "main", "java", "persistence", "todolists") + File.separator;
        assertEquals(expectedPath, actualPath);

        Field mapperField = ToDoListHandler.class.getDeclaredField("mapper");
        mapperField.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) mapperField.get(defaultHandler);
        assertNotNull(mapper);

        LocalDate testDate = LocalDate.now();
        String dateJson = mapper.writeValueAsString(testDate);
        LocalDate parsedDate = mapper.readValue(dateJson, LocalDate.class);
        assertEquals(testDate, parsedDate);
    }

    /**
     * Tests that assigning a to-do list to a user creates an empty file.
     */
    @Test
    @DisplayName("Assign to-do list creates empty file")
    @Tag("file")
    public void testAssignToDoList_CreatesEmptyFile() {
        toDoListHandler.assignToDoList(user);

        File expectedFile = new File(testToDoListFolderPath.toString(), user.getUsername() + ".json");
        assertTrue(expectedFile.exists());

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            ToDoList loadedList = mapper.readValue(expectedFile, ToDoList.class);
            assertNotNull(loadedList);
            assertTrue(loadedList.getTasks().isEmpty());
        } catch (IOException e) {
            fail("Failed to read the to-do list file.");
        }
    }

    /**
     * Tests loading a to-do list when the file exists.
     * Verifies that the tasks are correctly loaded from the file.
     */
    @Test
    @DisplayName("Load to-do list when file exists")
    @Tag("load")
    public void testLoadToDoList_FileExists() {
        toDoListHandler.assignToDoList(user);

        ToDoList toDoList = new ToDoList();
        toDoList.addTask(new Task("Sample Task1"));
        toDoList.addTask(new Task("Sample Task2", LocalDate.now(), "Description", "High"));
        toDoList.addTask(new Task("Sample Task3", null, "Description", "Low"));

        toDoListHandler.updateToDoList(user, toDoList);

        ToDoList loadedList = toDoListHandler.loadToDoList(user);

        assertNotNull(loadedList);
        assertEquals(3, loadedList.getTasks().size());
        assertEquals("Sample Task1", loadedList.getTasks().get(0).getTaskName());
        assertEquals("Description", loadedList.getTasks().get(1).getDescription());
        assertFalse(loadedList.getTasks().get(2).isCompleted());
    }

    /**
     * Tests that loading a to-do list that does not exist throws an exception with the correct message.
     */
    @Test
    @DisplayName("Throw exception when loading a to-do list that does not exist")
    @Tag("exception")
    public void testLoadToDoList_FileDoesNotExist() {
        File file = new File(testToDoListFolderPath.toString(), user.getUsername() + ".json");
        if (file.exists()) {
            file.delete();
        }

        Exception exception = assertThrows(IllegalArgumentException.class, () -> toDoListHandler.loadToDoList(user));

        String expectedMessage = "To-do list file does not exist for user: " + user.getUsername();
        assertEquals(expectedMessage, exception.getMessage());
    }

    /**
     * Tests updating a to-do list when the file does not exist.
     * Verifies that the file is created and tasks are saved correctly.
     */
    @Test
    @DisplayName("Update to-do list when file does not exist")
    @Tag("update")
    public void testUpdateToDoList_FileDoesNotExist() {
        File file = new File(testToDoListFolderPath.toString(), user.getUsername() + ".json");
        if (file.exists()) {
            file.delete();
        }

        ToDoList toDoList = new ToDoList();
        toDoList.addTask(new Task("Simple task"));

        toDoListHandler.updateToDoList(user, toDoList);

        assertTrue(file.exists());

        ToDoList loadedList = toDoListHandler.loadToDoList(user);
        assertNotNull(loadedList);
        assertEquals(1, loadedList.getTasks().size());
        assertEquals("Simple task", loadedList.getTasks().get(0).getTaskName());
    }

    /**
     * Tests that assigning a to-do list overwrites the existing file.
     */
    @Test
    @DisplayName("Assign to-do list overwrites existing file")
    @Tag("file")
    public void testAssignToDoList_OverwritesExistingFile() {
        ToDoList toDoList = new ToDoList();
        toDoList.addTask(new Task("Simple task"));
        toDoListHandler.assignToDoList(user);
        toDoListHandler.updateToDoList(user, toDoList);

        toDoListHandler.assignToDoList(user);

        ToDoList loadedList = toDoListHandler.loadToDoList(user);
        assertNotNull(loadedList);
        assertTrue(loadedList.getTasks().isEmpty());
    }

    /**
     * Tests updating a to-do list with an empty list.
     * Verifies that the to-do list file is updated accordingly.
     */
    @Test
    @DisplayName("Update to-do list with empty list")
    @Tag("update")
    public void testUpdateToDoList_WithEmptyList() {
        toDoListHandler.assignToDoList(user);

        ToDoList toDoList = new ToDoList();
        toDoListHandler.updateToDoList(user, toDoList);

        ToDoList loadedList = toDoListHandler.loadToDoList(user);
        assertNotNull(loadedList);
        assertTrue(loadedList.getTasks().isEmpty());
    }

    /**
     * Tests loading a group to-do list when the file does not exist.
     * Verifies that an empty ToDoList is returned.
     */
    @Test
    @DisplayName("Load group to-do list when file does not exist")
    @Tag("group")
    public void testLoadGroupToDoList_FileDoesNotExist() throws IOException{
        File file = new File(groupTestFolderPath.toString(), userGroup.getGroupName() + ".json");
        if (file.exists()) {
            file.delete();
        }

        userHandler.saveUser(user);
        groupHandler.createGroup(user, userGroup.getGroupName());
        ToDoList toDoList = toDoListHandler.loadGroupToDoList(userGroup);

        assertNotNull(toDoList);
        assertTrue(toDoList.getTasks().isEmpty());
    }

    /**
     * Tests updating and loading a group to-do list.
     * Verifies that tasks are correctly saved and loaded.
     */
    @Test
    @DisplayName("Update and load group to-do list")
    @Tag("group")
    public void testUpdateGroupToDoList_And_LoadGroupToDoList() throws IOException {
        ToDoList toDoList = new ToDoList();
        toDoList.addTask(new Task("Group Task 1"));
        toDoList.addTask(new Task("Group Task 2", LocalDate.now(), "Group Description", "High"));

        userHandler.saveUser(user);
        groupHandler.createGroup(user, userGroup.getGroupName());
        toDoListHandler.updateGroupToDoList(userGroup, toDoList);

        File file = new File(groupTestFolderPath.toString(), userGroup.getGroupName() + ".json");
        assertTrue(file.exists());

        ToDoList loadedToDoList = toDoListHandler.loadGroupToDoList(userGroup);

        assertNotNull(loadedToDoList);
        assertEquals(toDoList.getTasks().size(), loadedToDoList.getTasks().size());
        assertEquals(toDoList.getTasks().get(0).getTaskName(), loadedToDoList.getTasks().get(0).getTaskName());
        assertEquals(toDoList.getTasks().get(1).getDescription(), loadedToDoList.getTasks().get(1).getDescription());
    }

    /**
     * Tests loading a group to-do list when the file is corrupted.
     * Verifies that an empty ToDoList is returned.
     */
    @Test
    @DisplayName("Load group to-do list when file is corrupted")
    @Tag("group")
    public void testLoadGroupToDoList_FileIsCorrupted() throws IOException {
        File file = new File(groupTestFolderPath.toString(), userGroup.getGroupName() + ".json");
        Files.writeString(file.toPath(), "This is not valid JSON");

        userHandler.saveUser(user);
        groupHandler.createGroup(user, userGroup.getGroupName());
        ToDoList toDoList = toDoListHandler.loadGroupToDoList(userGroup);

        assertNotNull(toDoList);
        assertTrue(toDoList.getTasks().isEmpty());
    }
}

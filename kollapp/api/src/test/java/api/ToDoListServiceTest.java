package api;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import api.service.GroupService;
import api.service.ToDoListService;
import api.service.UserService;
import core.Task;
import core.ToDoList;
import core.User;
import core.UserGroup;

@Tag("unit")
public class ToDoListServiceTest {

    private User user;
    private UserGroup userGroup;
    
    private Path testToDoListFolderPath;
    private Path groupTestFolderPath;
    private Path userTestFolderPath;

    private ToDoListService toDoListService;
    private GroupService groupService;
    private UserService userService;

    @BeforeEach
    public void setUp() throws IOException {
        this.testToDoListFolderPath = Paths.get("..", "..", "persistence","src", "main", "java", "persistence", "todolists", "tests").toAbsolutePath()
        .normalize();
        this.groupTestFolderPath = Paths.get("..", "..", "persistence","src", "main", "java", "persistence", "grouptodolists", "tests").toAbsolutePath()
        .normalize();
        this.userTestFolderPath = Paths.get("..", "..", "persistence","src", "main", "java", "persistence", "users", "tests").toAbsolutePath()
        .normalize();

        toDoListService = new ToDoListService(testToDoListFolderPath, groupTestFolderPath);
        userService = new UserService(userTestFolderPath);
        groupService = new GroupService(groupTestFolderPath, groupTestFolderPath);

        user = new User("testUser", "password123");
        userGroup = new UserGroup("testGroup");

        Files.createDirectories(testToDoListFolderPath);
        Files.createDirectories(groupTestFolderPath);
        Files.createDirectories(userTestFolderPath);
    }

    @AfterEach
    public void tearDown() throws IOException {
        deleteDirectory(testToDoListFolderPath);
        deleteDirectory(groupTestFolderPath);
        deleteDirectory(userTestFolderPath);
    }

    private void deleteDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            try (Stream<Path> stream = Files.walk(path)) {
                stream.sorted(Comparator.reverseOrder())
                      .map(Path::toFile)
                      .forEach(file -> {
                          if (!file.delete()) {
                              System.out.println("Failed to delete: " + file.getAbsolutePath());
                          }
                      });
            }
        }
    }

    @Test
    @DisplayName("Assign to-do list creates empty file")
    @Tag("file")
    public void testAssignToDoList_CreatesEmptyFile() {
        toDoListService.assignToDoList(user.getUsername());

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
        toDoListService.assignToDoList(user.getUsername());

        ToDoList toDoList = new ToDoList();
        toDoList.addTask(new Task("Sample Task1"));
        toDoList.addTask(new Task("Sample Task2", LocalDate.now(), "Description", "High"));
        toDoList.addTask(new Task("Sample Task3", null, "Description", "Low"));

        toDoListService.updateToDoList(user.getUsername(), toDoList);

        ToDoList loadedList = toDoListService.loadToDoList(user.getUsername());

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

        Exception exception = assertThrows(IllegalArgumentException.class, () -> toDoListService.loadToDoList(user.getUsername()));

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

        toDoListService.updateToDoList(user.getUsername(), toDoList);

        assertTrue(file.exists());

        ToDoList loadedList = toDoListService.loadToDoList(user.getUsername());
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
        toDoListService.assignToDoList(user.getUsername());
        toDoListService.updateToDoList(user.getUsername(), toDoList);

        toDoListService.assignToDoList(user.getUsername());

        ToDoList loadedList = toDoListService.loadToDoList(user.getUsername());
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
        toDoListService.assignToDoList(user.getUsername());

        ToDoList toDoList = new ToDoList();
        toDoListService.updateToDoList(user.getUsername(), toDoList);

        ToDoList loadedList = toDoListService.loadToDoList(user.getUsername());
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

        userService.saveUser(user);
        groupService.createGroup(user.getUsername(), userGroup.getGroupName());
        ToDoList toDoList = toDoListService.loadGroupToDoList(userGroup.getGroupName());

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

        userService.saveUser(user);
        groupService.createGroup(user.getUsername(), userGroup.getGroupName());
        toDoListService.updateGroupToDoList(userGroup.getGroupName(), toDoList);

        File file = new File(groupTestFolderPath.toString(), userGroup.getGroupName() + ".json");
        assertTrue(file.exists());

        ToDoList loadedToDoList = toDoListService.loadGroupToDoList(userGroup.getGroupName());

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

        userService.saveUser(user);
        groupService.createGroup(user.getUsername(), userGroup.getGroupName());
        ToDoList toDoList = toDoListService.loadGroupToDoList(userGroup.getGroupName());

        assertNotNull(toDoList);
        assertTrue(toDoList.getTasks().isEmpty());
    }
}

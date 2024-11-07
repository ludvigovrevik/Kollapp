package api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import core.Task;
import core.ToDoList;
import core.User;
import core.UserGroup;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
public class ToDoListServiceTest {

    private User user;
    private UserGroup userGroup;

    @TempDir
    Path tempDir;

    private Path testToDoListFolderPath;
    private Path groupTestFolderPath;
    private Path userTestFolderPath;

    private ToDoListService toDoListService;
    private GroupService groupService;
    private UserService userService;

    @BeforeEach
    public void setUp() throws IOException {
        this.testToDoListFolderPath = tempDir.resolve("todolists");
        this.groupTestFolderPath = tempDir.resolve("grouptodolists");
        this.userTestFolderPath = tempDir.resolve("users");

        Files.createDirectories(testToDoListFolderPath);
        Files.createDirectories(groupTestFolderPath);
        Files.createDirectories(userTestFolderPath);

        userService = new UserService(userTestFolderPath);
        toDoListService = new ToDoListService(testToDoListFolderPath, groupTestFolderPath, userService);
        groupService = new GroupService(groupTestFolderPath, groupTestFolderPath, userService);

        user = new User("testUser", "password123");
        userGroup = new UserGroup("testGroup");

        userService.saveUser(user);
    }

    @AfterEach
    public void tearDown() throws IOException {
        deleteDirectory(tempDir);
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

        Path expectedFile = testToDoListFolderPath.resolve(user.getUsername() + ".json");
        assertTrue(Files.exists(expectedFile));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            ToDoList loadedList = mapper.readValue(expectedFile.toFile(), ToDoList.class);
            assertNotNull(loadedList);
            assertTrue(loadedList.getTasks().isEmpty());
        } catch (IOException e) {
            fail("Failed to read the to-do list file.");
        }
    }

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

    @Test
    @DisplayName("Throw exception when loading a to-do list that does not exist")
    @Tag("exception")
    public void testLoadToDoList_FileDoesNotExist() {
        Path file = testToDoListFolderPath.resolve(user.getUsername() + ".json");
        if (Files.exists(file)) {
            file.toFile().delete();
        }

        Exception exception = assertThrows(IllegalArgumentException.class, () -> toDoListService.loadToDoList(user.getUsername()));

        String expectedMessage = "To-do list file does not exist for user: " + user.getUsername();
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Update to-do list when file does not exist")
    @Tag("update")
    public void testUpdateToDoList_FileDoesNotExist() {
        Path file = testToDoListFolderPath.resolve(user.getUsername() + ".json");
        if (Files.exists(file)) {
            file.toFile().delete();
        }

        ToDoList toDoList = new ToDoList();
        toDoList.addTask(new Task("Simple task"));

        toDoListService.updateToDoList(user.getUsername(), toDoList);

        assertTrue(Files.exists(file));

        ToDoList loadedList = toDoListService.loadToDoList(user.getUsername());
        assertNotNull(loadedList);
        assertEquals(1, loadedList.getTasks().size());
        assertEquals("Simple task", loadedList.getTasks().get(0).getTaskName());
    }

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

    @Test
    @DisplayName("Load group to-do list when file does not exist")
    @Tag("group")
    public void testLoadGroupToDoList_FileDoesNotExist() throws IOException {
        Path file = groupTestFolderPath.resolve(userGroup.getGroupName() + ".json");
        if (Files.exists(file)) {
            file.toFile().delete();
        }

        groupService.createGroup(user.getUsername(), userGroup.getGroupName());
        ToDoList toDoList = toDoListService.loadGroupToDoList(userGroup.getGroupName());

        assertNotNull(toDoList);
        assertTrue(toDoList.getTasks().isEmpty());
    }

    @Test
    @DisplayName("Update and load group to-do list")
    @Tag("group")
    public void testUpdateGroupToDoList_And_LoadGroupToDoList() throws IOException {
        ToDoList toDoList = new ToDoList();
        toDoList.addTask(new Task("Group Task 1"));
        toDoList.addTask(new Task("Group Task 2", LocalDate.now(), "Group Description", "High"));

        groupService.createGroup(user.getUsername(), userGroup.getGroupName());
        toDoListService.updateGroupToDoList(userGroup.getGroupName(), toDoList);

        Path file = groupTestFolderPath.resolve(userGroup.getGroupName() + ".json");
        assertTrue(Files.exists(file));

        ToDoList loadedToDoList = toDoListService.loadGroupToDoList(userGroup.getGroupName());

        assertNotNull(loadedToDoList);
        assertEquals(toDoList.getTasks().size(), loadedToDoList.getTasks().size());
        assertEquals(toDoList.getTasks().get(0).getTaskName(), loadedToDoList.getTasks().get(0).getTaskName());
        assertEquals(toDoList.getTasks().get(1).getDescription(), loadedToDoList.getTasks().get(1).getDescription());
    }

    @Test
    @DisplayName("Load group to-do list when file is corrupted")
    @Tag("group")
    public void testLoadGroupToDoList_FileIsCorrupted() throws IOException {
        // Step 1: Create the group
        groupService.createGroup(user.getUsername(), userGroup.getGroupName());

        // Step 2: Corrupt the group to-do list file
        Path file = groupTestFolderPath.resolve(userGroup.getGroupName() + ".json");
        Files.writeString(file, "This is not valid JSON");

        // Step 3: Attempt to load the corrupted group to-do list
        Exception exception = assertThrows(IllegalArgumentException.class, () -> toDoListService.loadGroupToDoList(userGroup.getGroupName()));

        String expectedMessage = "Failed to load group to-do list for group: " + userGroup.getGroupName();
        assertEquals(expectedMessage, exception.getMessage());
    }
}
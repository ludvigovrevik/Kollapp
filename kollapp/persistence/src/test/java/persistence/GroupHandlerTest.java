package persistence;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.util.Comparator;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import core.ToDoList;
import core.User;
import core.UserGroup;

public class GroupHandlerTest {

    private GroupHandler groupHandler;
    private UserHandler userHandler;
    private User user;
    private User user2;
    private Path groupPath;
    private Path groupToDoListPath;
    private Path userPath;
    private Path toDoListPath;

    @BeforeEach
    public void setUp() throws IOException {
        // Set up test directories
        this.groupPath = Paths.get("src", "main", "java", "persistence", "groups", "tests");
        this.groupToDoListPath = Paths.get("src", "main", "java", "persistence", "grouptodolists", "tests");
        this.userPath = Paths.get("src", "main", "java", "persistence", "users", "tests");
        this.toDoListPath = Paths.get("src", "main", "java", "persistence", "todolists", "tests");

        // Create directories if they don't exist
        createDirectory(groupPath);
        createDirectory(groupToDoListPath);
        createDirectory(userPath);
        createDirectory(toDoListPath);

        // Initialize UserHandler and GroupHandler with test paths
        this.userHandler = new UserHandler(userPath.toString() + File.separator, toDoListPath.toString() + File.separator);
        this.groupHandler = new GroupHandler(groupPath.toString() + File.separator, groupToDoListPath.toString() + File.separator, userHandler);

        // Create test users
        user = new User("testUser1", "password123");
        user2 = new User("testUser2", "password456");

        // Save test users
        userHandler.saveUser(user);
        userHandler.saveUser(user2);
    }

    @AfterEach
    public void tearDown() throws IOException {
        // Delete the content in the test directories
        deleteDirectory(groupPath);
        deleteDirectory(groupToDoListPath);
        deleteDirectory(userPath);
        deleteDirectory(toDoListPath);
    }

    @Test
    public void testConstructors() throws Exception {
        // Create an instance using the default constructor
        GroupHandler defaultHandler = new GroupHandler();

        // Use reflection to access the private GROUP_PATH field
        Field groupPathField = GroupHandler.class.getDeclaredField("GROUP_PATH");
        groupPathField.setAccessible(true);
        String actualGroupPath = (String) groupPathField.get(defaultHandler);

        // Expected default group path
        String expectedGroupPath = Paths.get("..", "persistence", "src", "main", "java", "persistence", "groups")
                .toString() + File.separator;
        assertEquals(expectedGroupPath, actualGroupPath, "The default GROUP_PATH should be set correctly.");

        // Use reflection to access the private GROUP_TODOLIST_PATH field
        Field groupToDoListPathField = GroupHandler.class.getDeclaredField("GROUP_TODOLIST_PATH");
        groupToDoListPathField.setAccessible(true);
        String actualGroupToDoListPath = (String) groupToDoListPathField.get(defaultHandler);

        // Expected default group to-do list path
        String expectedGroupToDoListPath = Paths.get("..", "persistence", "src", "main", "java", "persistence", "grouptodolists")
                .toString() + File.separator;
        assertEquals(expectedGroupToDoListPath, actualGroupToDoListPath, "The default GROUP_TODOLIST_PATH should be set correctly.");

        // Use reflection to access the private mapper field
        Field mapperField = GroupHandler.class.getDeclaredField("mapper");
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
    void testCreateGroup() throws IOException {
        String groupName = "testGroup";

        // Create a group with user as the initial member
        groupHandler.createGroup(user, groupName);

        // Verify that the group file was created
        Path groupFilePath = groupPath.resolve(groupName + ".json");
        assertTrue(Files.exists(groupFilePath), "Group file should exist");

        // Verify that the group ToDoList file was created
        Path groupToDoListFilePath = groupToDoListPath.resolve(groupName + ".json");
        assertTrue(Files.exists(groupToDoListFilePath), "Group ToDoList file should exist");

        // Verify that the user has the group in their list
        User updatedUser = userHandler.getUser(user.getUsername()).get();
        assertTrue(updatedUser.getUserGroups().contains(groupName), "User should have the group in their list");

        // Load the group and verify its content
        UserGroup group = groupHandler.getGroup(groupName);
        assertNotNull(group, "Group should not be null");
        assertEquals(groupName, group.getGroupName(), "Group names should match");
        assertTrue(group.getUsers().contains(user.getUsername()), "Group should contain the initial user");
    }

    @Test
    void testGetGroup() throws IOException {
        String groupName = "testGroup";

        // Create a group first
        groupHandler.createGroup(user, groupName);

        // Get the group
        UserGroup group = groupHandler.getGroup(groupName);
        assertNotNull(group, "Group should not be null");
        assertEquals(groupName, group.getGroupName(), "Group names should match");
        assertTrue(group.getUsers().contains(user.getUsername()), "Group should contain the initial user");

        // Attempt to get a non-existent group
        assertThrows(IllegalArgumentException.class, () -> groupHandler.getGroup("nonExistentGroup"));
    }

    @Test
    void testAssignUserToGroup() throws IOException {
        String groupName = "testGroup";

        // Create a group with user as the initial member
        groupHandler.createGroup(user, groupName);

        // Assign user2 to the group
        groupHandler.assignUserToGroup(user2, groupName);

        // Verify that user2 has the group in their list
        User updatedUser2 = userHandler.getUser(user2.getUsername()).get();
        assertTrue(updatedUser2.getUserGroups().contains(groupName), "User2 should have the group in their list");

        // Verify that the group now contains user2
        UserGroup group = groupHandler.getGroup(groupName);
        assertNotNull(group, "Group should not be null");
        assertTrue(group.getUsers().contains(user2.getUsername()), "Group should contain user2" + group.getUsers());
    }

    // Helper methods for directory operations
    private void createDirectory(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    private void deleteDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                 .sorted(Comparator.reverseOrder())
                 .map(Path::toFile)
                 .forEach(File::delete);
        }
    }
}

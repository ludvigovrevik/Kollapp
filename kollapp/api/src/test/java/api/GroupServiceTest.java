
package api;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.util.Comparator;
import java.util.stream.Stream;

import org.junit.jupiter.api.*;

import api.service.GroupService;
import api.service.UserService;
import core.User;
import core.UserGroup;

/**
 * Unit tests for the {@link groupService} class.
 */
@Tag("unit")
public class GroupServiceTest {

    private GroupService groupService;
    private User user;
    private User user2;
    private Path groupPath;
    private Path groupToDoListPath;
    private Path userPath;
    private Path toDoListPath;

    /**
     * Sets up the test environment by initializing test directories and handlers.
     */
    @BeforeEach
    public void setUp() throws IOException {
        this.groupPath = Paths.get("..", "persistence", "src", "main", "java", "persistence", "groups", "tests");
        this.groupToDoListPath = Paths.get("..", "persistence","src", "main", "java", "persistence", "grouptodolists", "tests");
        this.userPath = Paths.get("..", "..", "persistence","src", "main", "java", "persistence", "users", "tests");
        this.toDoListPath = Paths.get("..", "..", "persistence","src", "main", "java", "persistence", "todolists", "tests");

        createDirectory(groupPath);
        createDirectory(groupToDoListPath);
        createDirectory(userPath);
        createDirectory(toDoListPath);

        UserService userService = new UserService(userPath);
        this.groupService = new GroupService(groupPath, groupToDoListPath);

        user = new User("testUser1", "password123");
        user2 = new User("testUser2", "password456");
        userService.saveUser(user);
        userService.saveUser(user2);
    }

    /**
     * Cleans up the test environment by deleting test directories after each test.
     */
    @AfterEach
    public void tearDown() throws IOException {
        deleteDirectory(groupPath);
        deleteDirectory(groupToDoListPath);
        deleteDirectory(userPath);
        deleteDirectory(toDoListPath);
    }

    /**
     * Tests the default constructor and verifies that default paths and
     * ObjectMapper are correctly initialized.
     */
    @Test
    @DisplayName("Test groupService default constructor initialization")
    @Tag("constructor")
    public void testConstructors() throws Exception {
        GroupService defaultHandler = new GroupService();

        // Verify groupPath field
        Field groupPathField = GroupService.class.getDeclaredField("groupPath");
        groupPathField.setAccessible(true);
        String actualGroupPath = (String) groupPathField.get(defaultHandler);
        String expectedGroupPath = Paths.get("..", "persistence", "src", "main", "java", "persistence", "groups").toAbsolutePath()
        .normalize() + File.separator;
        assertEquals(expectedGroupPath, actualGroupPath);

        // Verify groupToDoListPath field
        Field groupToDoListPathField = GroupService.class.getDeclaredField("groupToDoListPath");
        groupToDoListPathField.setAccessible(true);
        String actualGroupToDoListPath = (String) groupToDoListPathField.get(defaultHandler);
        String expectedGroupToDoListPath = Paths
                .get("..", "persistence", "src", "main", "java", "persistence", "grouptodolists").toAbsolutePath()
                .normalize() + File.separator;
        assertEquals(expectedGroupToDoListPath, actualGroupToDoListPath);
    }

    /**
     * Tests creating a new group and verifies that group files are created and user
     * is assigned correctly.
     */
    @Test
    @DisplayName("Test creating a new group")
    @Tag("group")
    void testCreateGroup() {
        String groupName = "testGroup";

        groupService.createGroup(user.getUsername(), groupName);

        Path groupFilePath = groupPath.resolve(groupName + ".json");
        assertTrue(Files.exists(groupFilePath));

        Path groupToDoListFilePath = groupToDoListPath.resolve(groupName + ".json");
        assertTrue(Files.exists(groupToDoListFilePath));

        UserGroup group = groupService.getGroup(groupName).get();
        assertNotNull(group);
        assertEquals(groupName, group.getGroupName());
        assertTrue(group.getUsers().contains(user.getUsername()));
    }

    /**
     * Tests retrieving an existing group and handling non-existent groups.
     */
    @Test
    @DisplayName("Test retrieving an existing group")
    @Tag("group")
    void testGetGroup() {
        String groupName = "testGroup";

        groupService.createGroup(user.getUsername(), groupName);
        UserGroup group = groupService.getGroup(groupName).get();

        assertNotNull(group);
        assertEquals(groupName, group.getGroupName());
        assertTrue(group.getUsers().contains(user.getUsername()));

        assertThrows(IllegalArgumentException.class, () -> groupService.getGroup("nonExistentGroup"));
    }

    /**
     * Tests assigning a user to an existing group and verifies that group data is
     * updated.
     */
    @Test
    @DisplayName("Test assigning a user to a group")
    @Tag("group")
    void testAssignUserToGroup() {
        String groupName = "testGroup";

        groupService.createGroup(user.getUsername(), groupName);
        groupService.assignUserToGroup(user2.getUsername(), groupName);

        UserGroup group = groupService.getGroup(groupName).get();
        assertNotNull(group);
        assertTrue(group.getUsers().contains(user2.getUsername()));
    }

    /**
     * Creates a directory if it does not already exist.
     *
     * @param path the path of the directory to create
     * @throws IOException if an I/O error occurs
     */
    private void createDirectory(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    /**
     * Deletes a directory and all its contents.
     *
     * @param path the path of the directory to delete
     * @throws IOException if an I/O error occurs
     */
    private void deleteDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            try (Stream<Path> groupStream = Files.walk(path)) {
                groupStream
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(file -> {
                            if (!file.delete()) {
                                System.out.println("Failed to delete: " + file.getAbsolutePath());
                            }
                        });
            }
        }
    }
}

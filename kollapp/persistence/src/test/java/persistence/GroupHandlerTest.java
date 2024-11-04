package persistence;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.util.Comparator;
import java.util.stream.Stream;

import org.junit.jupiter.api.*;

import core.User;
import core.UserGroup;

/**
 * Unit tests for the {@link GroupHandler} class.
 */
@Tag("unit")
public class GroupHandlerTest {

    private GroupHandler groupHandler;
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
        this.groupPath = Paths.get("src", "main", "java", "persistence", "groups", "tests");
        this.groupToDoListPath = Paths.get("src", "main", "java", "persistence", "grouptodolists", "tests");
        this.userPath = Paths.get("src", "main", "java", "persistence", "users", "tests");
        this.toDoListPath = Paths.get("src", "main", "java", "persistence", "todolists", "tests");

        createDirectory(groupPath);
        createDirectory(groupToDoListPath);
        createDirectory(userPath);
        createDirectory(toDoListPath);

        UserHandler userHandler = new UserHandler(
                userPath.toString() + File.separator
        );
        this.groupHandler = new GroupHandler(
                groupPath.toString() + File.separator,
                groupToDoListPath.toString() + File.separator,
                userHandler);

        user = new User("testUser1", "password123");
        user2 = new User("testUser2", "password456");
        userHandler.saveUser(user);
        userHandler.saveUser(user2);
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
    @DisplayName("Test GroupHandler default constructor initialization")
    @Tag("constructor")
    public void testConstructors() throws Exception {
        GroupHandler defaultHandler = new GroupHandler();

        // Verify groupPath field
        Field groupPathField = GroupHandler.class.getDeclaredField("groupPath");
        groupPathField.setAccessible(true);
        String actualGroupPath = (String) groupPathField.get(defaultHandler);
        String expectedGroupPath = Paths.get("..", "persistence", "src", "main", "java", "persistence", "groups") + File.separator;
        assertEquals(expectedGroupPath, actualGroupPath);

        // Verify groupToDoListPath field
        Field groupToDoListPathField = GroupHandler.class.getDeclaredField("groupToDoListPath");
        groupToDoListPathField.setAccessible(true);
        String actualGroupToDoListPath = (String) groupToDoListPathField.get(defaultHandler);
        String expectedGroupToDoListPath = Paths
                .get("..", "persistence", "src", "main", "java", "persistence", "grouptodolists") + File.separator;
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

        groupHandler.createGroup(user, groupName);

        Path groupFilePath = groupPath.resolve(groupName + ".json");
        assertTrue(Files.exists(groupFilePath));

        Path groupToDoListFilePath = groupToDoListPath.resolve(groupName + ".json");
        assertTrue(Files.exists(groupToDoListFilePath));

        UserGroup group = groupHandler.getGroup(groupName);
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

        groupHandler.createGroup(user, groupName);
        UserGroup group = groupHandler.getGroup(groupName);

        assertNotNull(group);
        assertEquals(groupName, group.getGroupName());
        assertTrue(group.getUsers().contains(user.getUsername()));

        assertThrows(IllegalArgumentException.class, () -> groupHandler.getGroup("nonExistentGroup"));
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

        groupHandler.createGroup(user, groupName);
        groupHandler.assignUserToGroup(user2, groupName);

        UserGroup group = groupHandler.getGroup(groupName);
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

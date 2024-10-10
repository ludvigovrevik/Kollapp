package persistence;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.util.Comparator;
import java.time.LocalDate;

import org.junit.jupiter.api.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import core.ToDoList;
import core.User;
import core.UserGroup;

/**
 * Unit tests for the {@link GroupHandler} class.
 */
@Tag("unit")
public class GroupHandlerTest {

    private GroupHandler groupHandler;
    private UserHandler userHandler;
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

        this.userHandler = new UserHandler(
                userPath.toString() + File.separator,
                toDoListPath.toString() + File.separator);
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
        String expectedGroupPath = Paths.get("..", "persistence", "src", "main", "java", "persistence", "groups")
                .toString() + File.separator;
        assertEquals(expectedGroupPath, actualGroupPath);

        // Verify groupToDoListPath field
        Field groupToDoListPathField = GroupHandler.class.getDeclaredField("groupToDoListPath");
        groupToDoListPathField.setAccessible(true);
        String actualGroupToDoListPath = (String) groupToDoListPathField.get(defaultHandler);
        String expectedGroupToDoListPath = Paths
                .get("..", "persistence", "src", "main", "java", "persistence", "grouptodolists")
                .toString() + File.separator;
        assertEquals(expectedGroupToDoListPath, actualGroupToDoListPath);

        // Verify ObjectMapper
        Field mapperField = GroupHandler.class.getDeclaredField("mapper");
        mapperField.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) mapperField.get(defaultHandler);
        assertNotNull(mapper);

        // Verify JavaTimeModule registration
        LocalDate testDate = LocalDate.now();
        String dateJson = mapper.writeValueAsString(testDate);
        LocalDate parsedDate = mapper.readValue(dateJson, LocalDate.class);
        assertEquals(testDate, parsedDate);
    }

    /**
     * Tests creating a new group and verifies that group files are created and user
     * is assigned correctly.
     */
    @Test
    @DisplayName("Test creating a new group")
    @Tag("group")
    void testCreateGroup() throws IOException {
        String groupName = "testGroup";

        groupHandler.createGroup(user, groupName);

        Path groupFilePath = groupPath.resolve(groupName + ".json");
        assertTrue(Files.exists(groupFilePath));

        Path groupToDoListFilePath = groupToDoListPath.resolve(groupName + ".json");
        assertTrue(Files.exists(groupToDoListFilePath));

        User updatedUser = userHandler.getUser(user.getUsername()).get();
        assertTrue(updatedUser.getUserGroups().contains(groupName));

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
    void testGetGroup() throws IOException {
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
    void testAssignUserToGroup() throws IOException {
        String groupName = "testGroup";

        groupHandler.createGroup(user, groupName);
        groupHandler.assignUserToGroup(user2, groupName);

        User updatedUser2 = userHandler.getUser(user2.getUsername()).get();
        assertTrue(updatedUser2.getUserGroups().contains(groupName));

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
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }
}

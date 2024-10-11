package persistence;

import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Optional;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.User;

/**
 * Unit tests for the UserHandler class.
 */
@Tag("persistence")
public class UserHandlerTest {

    private UserHandler userHandler;
    private User user;
    private Path toDoListPath;
    private Path userPath;

    /**
     * Sets up the necessary test environment by creating directories
     * and initializing the UserHandler before each test.
     */
    @BeforeEach
    public void setUp() throws IOException {
        this.toDoListPath = Paths.get("src", "main", "java", "persistence", "todolists", "tests");
        this.userPath = Paths.get("src", "main", "java", "persistence", "users", "tests");

        this.userHandler = new UserHandler(userPath.toString() + File.separator, toDoListPath.toString() + File.separator);
        user = new User("testUser", "password123");

        if (!Files.exists(toDoListPath)) {
            Files.createDirectories(toDoListPath);
        }
        if (!Files.exists(userPath)) {
            Files.createDirectories(userPath);
        }
    }

    /**
     * Cleans up the test environment after each test by deleting the created directories.
     */
    @AfterEach
    public void tearDown() throws IOException {
        if (Files.exists(userPath)) {
            Files.walk(userPath).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
        if (Files.exists(toDoListPath)) {
            Files.walk(toDoListPath).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
    }

    @Test
    @DisplayName("Test UserHandler constructors and default paths")
    void testConstructors() throws Exception {
        UserHandler defaultHandler = new UserHandler();

        Field userPathField = UserHandler.class.getDeclaredField("userPath");
        userPathField.setAccessible(true);
        String actualUserPath = (String) userPathField.get(defaultHandler);
        String expectedUserPath = Paths.get("..", "persistence", "src", "main", "java", "persistence", "users")
                .toString() + File.separator;
        assertEquals(expectedUserPath, actualUserPath);

        Field toDoListPathField = UserHandler.class.getDeclaredField("toDoListPath");
        toDoListPathField.setAccessible(true);
        String actualToDoListPath = (String) toDoListPathField.get(defaultHandler);
        String expectedToDoListPath = Paths.get("..", "persistence", "src", "main", "java", "persistence", "todolists")
                .toString() + File.separator;
        assertEquals(expectedToDoListPath, actualToDoListPath);

        Field mapperField = UserHandler.class.getDeclaredField("mapper");
        mapperField.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) mapperField.get(defaultHandler);
        assertNotNull(mapper);

        LocalDate testDate = LocalDate.now();
        String dateJson = mapper.writeValueAsString(testDate);
        LocalDate parsedDate = mapper.readValue(dateJson, LocalDate.class);
        assertEquals(testDate, parsedDate);
    }

    @Test
    @DisplayName("Test confirmation of new valid user")
    void testConfirmNewValidUser() {
        assertTrue(userHandler.confirmNewValidUser("testUser", "password123", "password123"));
        assertFalse(userHandler.confirmNewValidUser("", "password123", "password123"));
        assertFalse(userHandler.confirmNewValidUser("testUser", "", "password123"));
        assertFalse(userHandler.confirmNewValidUser("testUser", "password123", ""));
        assertFalse(userHandler.confirmNewValidUser("us", "password123", "password123"));
        assertFalse(userHandler.confirmNewValidUser("testUser", "password123", "password124"));
        assertFalse(userHandler.confirmNewValidUser("testUser", "passo", "passo"));
    }

    @Test
    @DisplayName("Test retrieving an existing user")
    void testGetUser() throws IOException {
        userHandler.saveUser(user);
        Optional<User> retrievedUser = userHandler.getUser(user.getUsername());
        assertTrue(retrievedUser.isPresent());
        assertEquals(user.getUsername(), retrievedUser.get().getUsername());
        assertEquals(user.getPassword(), retrievedUser.get().getPassword());
        Optional<User> nonExistingUser = userHandler.getUser("nonExistingUser");
        assertFalse(nonExistingUser.isPresent());
    }

    @Test
    @DisplayName("Test user validation error messages")
    void testGetUserValidationErrorMessage() throws IOException {
        String username = "newUser";
        String password = "password123";
        String confirmPassword = "password123";

        assertFalse(userHandler.userExists(username));
        assertNull(userHandler.getUserValidationErrorMessage(username, password, confirmPassword));

        userHandler.saveUser(new User(username, password));
        assertEquals("User already exists", userHandler.getUserValidationErrorMessage(username, password, confirmPassword));

        assertEquals("Fields cannot be empty", userHandler.getUserValidationErrorMessage("", password, confirmPassword));
        assertEquals("Fields cannot be empty", userHandler.getUserValidationErrorMessage(username, "", confirmPassword));
        assertEquals("Fields cannot be empty", userHandler.getUserValidationErrorMessage(username, password, ""));
        assertEquals("Username must be at least 3 characters long", userHandler.getUserValidationErrorMessage("us", password, confirmPassword));
        assertEquals("Passwords do not match", userHandler.getUserValidationErrorMessage("new-test-user", password, "differentPassword"));
        assertEquals("Password must be at least 6 characters long", userHandler.getUserValidationErrorMessage("new-test-user", "passo", "passo"));
    }

    @Test
    @DisplayName("Test loading an existing user")
    void testLoadUser() throws IOException {
        userHandler.saveUser(user);
        User loadedUser = userHandler.loadUser(user.getUsername(), user.getPassword());
        assertNotNull(loadedUser);
        assertEquals(user.getUsername(), loadedUser.getUsername());
        assertEquals(user.getPassword(), loadedUser.getPassword());

        User incorrectPasswordUser = userHandler.loadUser(user.getUsername(), "wrongPassword");
        assertNull(incorrectPasswordUser);

        User nonExistingUser = userHandler.loadUser("nonExistingUser", "password123");
        assertNull(nonExistingUser);
    }

    @Test
    @DisplayName("Test saving a new user")
    void testSaveUser() throws IOException {
        userHandler.saveUser(user);
        Path userFilePath = userPath.resolve(user.getUsername() + ".json");
        assertTrue(Files.exists(userFilePath));

        assertThrows(IllegalArgumentException.class, () -> userHandler.saveUser(user));
    }

    @Test
    @DisplayName("Test updating an existing user")
    void testUpdateUser() throws IOException {
        userHandler.saveUser(user);
        user.addUserGroup("Test group");
        userHandler.updateUser(user);

        User updatedUser = userHandler.loadUser(user.getUsername(), "password123");
        assertNotNull(updatedUser);
        assertEquals(user.getUsername(), updatedUser.getUsername());
        assertEquals(user.getPassword(), updatedUser.getPassword());
        assertEquals(user.getUserGroups(), updatedUser.getUserGroups());
    }

    @Test
    @DisplayName("Test checking if a user exists")
    void testUserExists() throws IOException {
        assertFalse(userHandler.userExists(user.getUsername()));

        userHandler.saveUser(user);
        assertTrue(userHandler.userExists(user.getUsername()));

        assertFalse(userHandler.userExists("nonExistingUser"));
    }
}

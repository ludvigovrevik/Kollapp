package persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.User;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link UserHandler} class.
 */
@Tag("unit")
public class UserHandlerTest {

    private UserHandler userHandler;
    private User user;
    private Path toDoListPath;
    private Path userPath;

    /**
     * Sets up the test environment by initializing paths and creating necessary directories.
     */
    @BeforeEach
    public void setUp() throws IOException {
        this.toDoListPath = Paths.get("src", "main", "java", "persistence", "todolists", "tests");
        this.userPath = Paths.get("src", "main", "java", "persistence", "users", "tests");

        this.userHandler = new UserHandler(
            userPath + File.separator
        );
        user = new User("testUser", "password123");

        if (!Files.exists(toDoListPath)) {
            Files.createDirectories(toDoListPath);
        }
        if (!Files.exists(userPath)) {
            Files.createDirectories(userPath);
        }
    }

    /**
     * Cleans up the test environment by deleting created directories after each test.
     */
    @AfterEach
    public void tearDown() throws IOException {
        if (Files.exists(userPath)) {
            try (Stream<Path> userStream = Files.walk(userPath)) {
                userStream
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(file -> {
                            if (!file.delete()) {
                                System.out.println("Failed to delete: " + file.getAbsolutePath());
                            }
                        });
            }
        }

        if (Files.exists(toDoListPath)) {
            try (Stream<Path> toDoListStream = Files.walk(toDoListPath)) {
                toDoListStream
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

    /**
     * Tests the default constructor and ensures paths and ObjectMapper are correctly initialized.
     */
    @Test
    @DisplayName("Test UserHandler constructors and default paths")
    @Tag("constructor")
    void testConstructors() throws Exception {
        UserHandler defaultHandler = new UserHandler();

        Field userPathField = UserHandler.class.getDeclaredField("userPath");
        userPathField.setAccessible(true);
        String actualUserPath = (String) userPathField.get(defaultHandler);
        String expectedUserPath = Paths.get("..", "persistence", "src", "main", "java", "persistence", "users")
                .toString() + File.separator;
        assertEquals(expectedUserPath, actualUserPath);

        Field mapperField = UserHandler.class.getDeclaredField("mapper");
        mapperField.setAccessible(true);
        ObjectMapper mapper = (ObjectMapper) mapperField.get(defaultHandler);
        assertNotNull(mapper);

        LocalDate testDate = LocalDate.now();
        String dateJson = mapper.writeValueAsString(testDate);
        LocalDate parsedDate = mapper.readValue(dateJson, LocalDate.class);
        assertEquals(testDate, parsedDate);
    }

    /**
     * Tests the confirmation of a new valid user with various input scenarios.
     */
    @Test
    @DisplayName("Test confirmation of new valid user")
    @Tag("validation")
    void testConfirmNewValidUser() {
        assertTrue(userHandler.confirmNewValidUser("testUser", "password123", "password123"));
        assertFalse(userHandler.confirmNewValidUser("", "password123", "password123"));
        assertFalse(userHandler.confirmNewValidUser("testUser", "", "password123"));
        assertFalse(userHandler.confirmNewValidUser("testUser", "password123", ""));
        assertFalse(userHandler.confirmNewValidUser("us", "password123", "password123"));
        assertFalse(userHandler.confirmNewValidUser("testUser", "password123", "password124"));
        assertFalse(userHandler.confirmNewValidUser("testUser", "passo", "passo"));
    }

    /**
     * Tests retrieving an existing user and ensures correct user data is returned.
     */
    @Test
    @DisplayName("Test retrieving an existing user")
    @Tag("persistence")
    void testGetUser() throws IOException {
        userHandler.saveUser(user);
        Optional<User> retrievedUser = userHandler.getUser(user.getUsername());
        assertTrue(retrievedUser.isPresent());
        assertEquals(user.getUsername(), retrievedUser.get().getUsername());
        assertEquals(user.getPassword(), retrievedUser.get().getPassword());

        Optional<User> nonExistingUser = userHandler.getUser("nonExistingUser");
        assertFalse(nonExistingUser.isPresent());
    }

    /**
     * Tests user validation error messages for various invalid input scenarios.
     */
    @Test
    @DisplayName("Test user validation error messages")
    @Tag("validation")
    void testGetUserValidationErrorMessage() throws IOException {
        String username = "newUser";
        String password = "password123";
        String confirmPassword = "password123";

        assertFalse(userHandler.userExists(username));
        assertNull(userHandler.getUserValidationErrorMessage(username, password, confirmPassword));

        userHandler.saveUser(new User(username, password));
        assertEquals("User already exists",
            userHandler.getUserValidationErrorMessage(username, password, confirmPassword));

        assertEquals("Fields cannot be empty",
            userHandler.getUserValidationErrorMessage("", password, confirmPassword));
        assertEquals("Fields cannot be empty",
            userHandler.getUserValidationErrorMessage(username, "", confirmPassword));
        assertEquals("Fields cannot be empty",
            userHandler.getUserValidationErrorMessage(username, password, ""));
        assertEquals("Username must be at least 3 characters long",
            userHandler.getUserValidationErrorMessage("us", password, confirmPassword));
        assertEquals("Passwords do not match",
            userHandler.getUserValidationErrorMessage("new-test-user", password, "differentPassword"));
        assertEquals("Password must be at least 6 characters long",
            userHandler.getUserValidationErrorMessage("new-test-user", "passo", "passo"));
    }

    /**
     * Tests loading an existing user and validates password matching.
     */
    @Test
    @DisplayName("Test loading an existing user")
    @Tag("persistence")
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

    /**
     * Tests saving a new user and checks for exceptions when saving an existing user.
     */
    @Test
    @DisplayName("Test saving a new user")
    @Tag("persistence")
    void testSaveUser() throws IOException {
        userHandler.saveUser(user);
        Path userFilePath = userPath.resolve(user.getUsername() + ".json");
        assertTrue(Files.exists(userFilePath));

        assertThrows(IllegalArgumentException.class, () -> userHandler.saveUser(user));
    }

    /**
     * Tests updating an existing user and verifies that changes are persisted.
     */
    @Test
    @DisplayName("Test updating an existing user")
    @Tag("persistence")
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

    /**
     * Tests checking if a user exists in the system.
     */
    @Test
    @DisplayName("Test checking if a user exists")
    @Tag("validation")
    void testUserExists() throws IOException {
        assertFalse(userHandler.userExists(user.getUsername()));

        userHandler.saveUser(user);
        assertTrue(userHandler.userExists(user.getUsername()));

        assertFalse(userHandler.userExists("nonExistingUser"));
    }
}

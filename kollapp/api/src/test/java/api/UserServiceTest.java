package api;

import com.fasterxml.jackson.databind.ObjectMapper;

import api.service.GroupService;
import api.service.UserService;
import core.User;
import org.junit.jupiter.api.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
 * Unit tests for the {@link UserService} class.
 */
@Tag("unit")
public class UserServiceTest {

    private UserService userService;
    private User user;
    private Path toDoListPath;
    private Path userPath;

    /**
     * Sets up the test environment by initializing paths and creating necessary directories.
     */
    @BeforeEach
    public void setUp() throws IOException {
        this.userPath = Paths.get("..", "..", "persistence","src", "main", "java", "persistence", "users", "tests").toAbsolutePath().normalize();
        this.toDoListPath = Paths.get("..", "..", "persistence","src", "main", "java", "persistence", "todolists", "tests").toAbsolutePath().normalize();

        createDirectory(userPath);
        createDirectory(toDoListPath);

        this.userService = new UserService(userPath);

        user = new User("testUser1", "password123");
        userService.saveUser(user);   // Ensure that this saves to the correct path
    }

    /**
     * Creates a directory if it does not exist.
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
    @DisplayName("Test UserService constructors and default paths")
    @Tag("constructor")
    void testConstructors() throws Exception {
        UserService defaultHandler = new UserService();

        Field userPathField = UserService.class.getDeclaredField("userPath");
        userPathField.setAccessible(true);
        String actualUserPath = (String) userPathField.get(defaultHandler);
        String expectedUserPath = Paths.get("..", "persistence", "src", "main", "java", "persistence", "users") + File.separator;
        assertEquals(expectedUserPath, actualUserPath);

        Field mapperField = UserService.class.getDeclaredField("mapper");
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
        assertTrue(userService.confirmNewValidUser("testUser", "password123", "password123"));
        assertFalse(userService.confirmNewValidUser("", "password123", "password123"));
        assertFalse(userService.confirmNewValidUser("testUser", "", "password123"));
        assertFalse(userService.confirmNewValidUser("testUser", "password123", ""));
        assertFalse(userService.confirmNewValidUser("us", "password123", "password123"));
        assertFalse(userService.confirmNewValidUser("testUser", "password123", "password124"));
        assertFalse(userService.confirmNewValidUser("testUser", "passo", "passo"));
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

        assertFalse(userService.userExists(username));
        assertNull(userService.getUserValidationErrorMessage(username, password, confirmPassword));

        userService.saveUser(new User(username, password));
        assertEquals("User already exists",
            userService.getUserValidationErrorMessage(username, password, confirmPassword));

        assertEquals("Fields cannot be empty",
            userService.getUserValidationErrorMessage("", password, confirmPassword));
        assertEquals("Fields cannot be empty",
            userService.getUserValidationErrorMessage(username, "", confirmPassword));
        assertEquals("Fields cannot be empty",
            userService.getUserValidationErrorMessage(username, password, ""));
        assertEquals("Username must be at least 3 characters long",
            userService.getUserValidationErrorMessage("us", password, confirmPassword));
        assertEquals("Passwords do not match",
            userService.getUserValidationErrorMessage("new-test-user", password, "differentPassword"));
        assertEquals("Password must be at least 6 characters long",
            userService.getUserValidationErrorMessage("new-test-user", "passo", "passo"));
    }

    @Test
    @DisplayName("Test loading an existing user")
    @Tag("persistence")
    void testLoadUser() throws IOException {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        
        // Create a user with a plain password and save it
        String plainPassword = user.getHashedPassword(); // Assuming this holds the plain password
        User userWithPlainPassword = new User(user.getUsername(), plainPassword);
        
        userService.saveUser(userWithPlainPassword);

        // Attempt to load the user with the correct password
        Optional<User> loadedUserOptional = userService.loadUser(user.getUsername(), plainPassword);
        assertTrue(loadedUserOptional.isPresent());
        User loadedUser = loadedUserOptional.get();

        assertEquals(user.getUsername(), loadedUser.getUsername());
        assertTrue(passwordEncoder.matches(plainPassword, loadedUser.getHashedPassword()));

        // Attempt to load the user with an incorrect password
        Optional<User> incorrectPasswordUser = userService.loadUser(user.getUsername(), "wrongPassword");
        assertFalse(incorrectPasswordUser.isPresent());

        // Attempt to load a non-existing user
        Optional<User> nonExistingUser = userService.loadUser("nonExistingUser", "password123");
        assertFalse(nonExistingUser.isPresent());
    }

    /**
     * Tests saving a new user and checks for exceptions when saving an existing user.
     */
    @Test
    @DisplayName("Test saving a new user")
    @Tag("persistence")
    void testSaveUser() throws IOException {
        userService.saveUser(user);
        Path userFilePath = userPath.resolve(user.getUsername() + ".json");
        assertTrue(Files.exists(userFilePath));

        assertThrows(IllegalArgumentException.class, () -> userService.saveUser(user));
    }

    /**
     * Tests checking if a user exists in the system.
     */
    @Test
    @DisplayName("Test checking if a user exists")
    @Tag("validation")
    void testUserExists() throws IOException {
        assertFalse(userService.userExists(user.getUsername()));

        userService.saveUser(user);
        assertTrue(userService.userExists(user.getUsername()));

        assertFalse(userService.userExists("nonExistingUser"));
    }
}

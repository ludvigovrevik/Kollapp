package persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import core.User;

public class UserHandlerTest {

    private UserHandler userHandler;
    private User user;
    private Path toDoListPath;
    private Path userPath;

    @BeforeEach
    public void setUp() throws IOException {
        // Use a test-specific directory to avoid interfering with real data
        this.toDoListPath = Paths.get("src", "main", "java", "persistence", "todolists", "tests");
        this.userPath = Paths.get("src", "main", "java", "persistence", "users", "tests");

        // Initialize UserHandler with the test path
        this.userHandler = new UserHandler(userPath.toString() + File.separator, toDoListPath.toString() + File.separator);

        user = new User("testUser", "password123");

        // Create the test directories if they don't exist
        if (!Files.exists(toDoListPath)) {
            Files.createDirectories(toDoListPath);
        }
        if (!Files.exists(userPath)) {
            Files.createDirectories(userPath);
        }
    }

    @AfterEach
    public void tearDown() throws IOException {
        // Delete the content in the test directory
        if (Files.exists(userPath)) {
            Files.walk(userPath)
                 .sorted(Comparator.reverseOrder())
                 .map(Path::toFile)
                 .forEach(File::delete);
        }
        if (Files.exists(toDoListPath)) {
            Files.walk(toDoListPath)
                 .sorted(Comparator.reverseOrder())
                 .map(Path::toFile)
                 .forEach(File::delete);
        }
    }

    @Test
    void testConfirmNewValidUser() {
        // Test valid user data
        assertTrue(userHandler.confirmNewValidUser("testUser", "password123", "password123"));

        // Test empty username
        assertFalse(userHandler.confirmNewValidUser("", "password123", "password123"));

        // Test empty password
        assertFalse(userHandler.confirmNewValidUser("testUser", "", "password123"));

        // Test empty confirmPassword
        assertFalse(userHandler.confirmNewValidUser("testUser", "password123", ""));

        // Test username length less than 4
        assertFalse(userHandler.confirmNewValidUser("usr", "password123", "password123"));

        // Test passwords do not match
        assertFalse(userHandler.confirmNewValidUser("testUser", "password123", "password124"));

        // Test password length less than 8
        assertFalse(userHandler.confirmNewValidUser("testUser", "pass123", "pass123"));
    }

    @Test
    void testGetUser() throws IOException {
        // Save a user first
        userHandler.saveUser(user);

        // Get the user
        Optional<User> retrievedUser = userHandler.getUser(user.getUsername());
        assertTrue(retrievedUser.isPresent());
        assertEquals(user.getUsername(), retrievedUser.get().getUsername());
        assertEquals(user.getPassword(), retrievedUser.get().getPassword());

        // Try to get a non-existing user
        Optional<User> nonExistingUser = userHandler.getUser("nonExistingUser");
        assertFalse(nonExistingUser.isPresent());
    }

    @Test
    void testGetUserValidationErrorMessage() throws IOException {
        String username = "newUser";
        String password = "password123";
        String confirmPassword = "password123";

        // Ensure user does not exist
        assertFalse(userHandler.userExists(username));

        // Should return null (no error)
        assertNull(userHandler.getUserValidationErrorMessage(username, password, confirmPassword));

        // Save the user
        userHandler.saveUser(new User(username, password));

        // Now user exists, should return "User already exists"
        assertEquals("User already exists", userHandler.getUserValidationErrorMessage(username, password, confirmPassword));

        // Test empty fields
        assertEquals("Fields cannot be empty", userHandler.getUserValidationErrorMessage("", password, confirmPassword));
        assertEquals("Fields cannot be empty", userHandler.getUserValidationErrorMessage(username, "", confirmPassword));
        assertEquals("Fields cannot be empty", userHandler.getUserValidationErrorMessage(username, password, ""));

        // Test username length less than 4
        assertEquals("Username must be at least 4 characters long", userHandler.getUserValidationErrorMessage("usr", password, confirmPassword));

        // Test passwords do not match
        assertEquals("Passwords do not match", userHandler.getUserValidationErrorMessage("new-test-user", password, "differentPassword"));

        // Test password length less than 8
        assertEquals("Password must be at least 8 characters long", userHandler.getUserValidationErrorMessage("new-test-user", "pass123", "pass123"));
    }

    @Test
    void testLoadUser() throws IOException {
        // Save a user
        userHandler.saveUser(user);

        // Load the user with correct password
        User loadedUser = userHandler.loadUser(user.getUsername(), user.getPassword());
        assertNotNull(loadedUser);
        assertEquals(user.getUsername(), loadedUser.getUsername());
        assertEquals(user.getPassword(), loadedUser.getPassword());

        // Load the user with incorrect password
        User incorrectPasswordUser = userHandler.loadUser(user.getUsername(), "wrongPassword");
        assertNull(incorrectPasswordUser);

        // Load a non-existing user
        User nonExistingUser = userHandler.loadUser("nonExistingUser", "password123");
        assertNull(nonExistingUser);
    }

    @Test
    void testSaveUser() throws IOException {
        // Save the user
        userHandler.saveUser(user);

        // Check that the file exists
        Path userFilePath = userPath.resolve(user.getUsername() + ".json");
        assertTrue(Files.exists(userFilePath));

        // Try saving the same user again, should throw an exception
        assertThrows(IllegalArgumentException.class, () -> userHandler.saveUser(user));
    }

    @Test
    void testUpdateUser() throws IOException {
        // Save the user
        userHandler.saveUser(user);

        // Update user data
        user.addUserGroup("Test group");
        userHandler.updateUser(user);

        // Load the user and check if the usergroup was added
        User updatedUser = userHandler.loadUser(user.getUsername(), "password123");
        assertNotNull(updatedUser);
        assertEquals(user.getUsername(), updatedUser.getUsername());
        assertEquals(user.getPassword(), updatedUser.getPassword());
        assertEquals(user.getUserGroups(), updatedUser.getUserGroups());
    }

    @Test
    void testUserExists() throws IOException {
        // Initially, user should not exist
        assertFalse(userHandler.userExists(user.getUsername()));

        // Save the user
        userHandler.saveUser(user);

        // Now, user should exist
        assertTrue(userHandler.userExists(user.getUsername()));

        // Check for a non-existing user
        assertFalse(userHandler.userExists("nonExistingUser"));
    }
}

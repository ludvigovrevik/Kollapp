package api.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import core.User;
import org.junit.jupiter.api.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for the {@link UserService} class.
 */
@Tag("unit")
public class UserServiceTest {
    @TempDir
    Path tempDir;
    
    private Path userPath;
    private UserService userService;
    private User user;

    @BeforeEach
    public void setUp() throws IOException {
        // Create users directory in temp directory
        this.userPath = tempDir.resolve("users");
        Files.createDirectories(userPath);

        // Initialize service with test directory
        this.userService = new UserService();
        ReflectionTestUtils.setField(userService, "userPath", userPath.toString() + File.separator);

        // Create test user
        user = new User("testUser1", "password123");
        userService.saveUser(user);
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
        assertEquals("", userService.getUserValidationErrorMessage(username, password, confirmPassword));

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

        // Attempt to load the user with the correct password
        Optional<User> loadedUserOptional = userService.loadUser(user.getUsername(), "password123");
        assertTrue(loadedUserOptional.isPresent());
        User loadedUser = loadedUserOptional.get();

        assertEquals(user.getUsername(), loadedUser.getUsername());
        assertTrue(passwordEncoder.matches("password123", loadedUser.getHashedPassword()));

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
        // User already saved in setUp()
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
        // Since we have already saved user in setUp()
        assertTrue(userService.userExists(user.getUsername()));

        assertFalse(userService.userExists("nonExistingUser"));
    }

    @Test
    @DisplayName("Test assign group to user and update user")
    @Tag("update")
    void testAssignGroupToUser() throws IOException {
        // Assign a group to the user
        String groupName = "testGroup";
        userService.assignGroupToUser(user.getUsername(), groupName);
        
        // Retrieve the updated user and verify the group was added
        Optional<User> updatedUserOpt = userService.getUser(user.getUsername());
        assertTrue(updatedUserOpt.isPresent());
        User updatedUser = updatedUserOpt.get();
        assertTrue(updatedUser.getUserGroups().contains(groupName));
        
        // Test assigning group to non-existent user
        String nonExistentUser = "nonExistentUser";
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.assignGroupToUser(nonExistentUser, groupName)
        );
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("Test removing a user")
    @Tag("remove")
    void testRemoveUser() throws IOException {
        // Verify user exists before removal
        assertTrue(userService.userExists(user.getUsername()));
        
        // Remove the user
        userService.removeUser(user.getUsername());
        
        // Verify user no longer exists
        assertFalse(userService.userExists(user.getUsername()));
        
        // Verify user file is actually deleted
        Path userFilePath = userPath.resolve(user.getUsername() + ".json");
        assertFalse(Files.exists(userFilePath));
        
        // Test removing non-existent user (should not throw exception)
        assertDoesNotThrow(() -> userService.removeUser("nonExistentUser"));
    }

    @Test
    @DisplayName("Test multiple group assignments and edge cases")
    @Tag("groups")
    void testMultipleGroupAssignments() throws IOException {
        // Assign multiple groups
        String[] groupNames = {"group1", "group2", "group3"};
        
        for (String groupName : groupNames) {
            userService.assignGroupToUser(user.getUsername(), groupName);
        }
        
        // Verify all groups were assigned
        Optional<User> updatedUserOpt = userService.getUser(user.getUsername());
        assertTrue(updatedUserOpt.isPresent());
        User updatedUser = updatedUserOpt.get();
        
        for (String groupName : groupNames) {
            assertTrue(updatedUser.getUserGroups().contains(groupName));
        }
        
        // Verify assigning same group twice doesn't create duplicate
        userService.assignGroupToUser(user.getUsername(), groupNames[0]);
        updatedUserOpt = userService.getUser(user.getUsername());
        assertTrue(updatedUserOpt.isPresent());
        assertEquals(3, updatedUserOpt.get().getUserGroups().size());
        
        // Test with null group name
        assertThrows(IllegalArgumentException.class, 
            () -> userService.assignGroupToUser(user.getUsername(), null));
    }

    @Test
    @DisplayName("Test constructor with custom path")
    @Tag("constructor")
    void testCustomPathConstructor() throws Exception {
        // Create a new temporary directory for this test
        Path customPath = tempDir.resolve("customUsers");
        Files.createDirectories(customPath);
        
        // Create service with custom path
        UserService customUserService = new UserService(customPath);
        
        // Test the service by saving and retrieving a user
        User testUser = new User("customUser", "password123");
        customUserService.saveUser(testUser);
        
        // Verify user file was created in correct location
        Path userFilePath = customPath.resolve(testUser.getUsername() + ".json");
        assertTrue(Files.exists(userFilePath));
        
        // Verify we can retrieve the user
        assertTrue(customUserService.userExists(testUser.getUsername()));
        Optional<User> loadedUser = customUserService.loadUser(testUser.getUsername(), "password123");
        assertTrue(loadedUser.isPresent());
        assertEquals(testUser.getUsername(), loadedUser.get().getUsername());
    }
}
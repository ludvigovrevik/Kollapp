package api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
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

@Tag("unit")
class UserServiceTest {

    @TempDir
    Path tempDir;

    private Path userPath;
    private UserService userService;
    private User user;

    @BeforeEach
    @DisplayName("Set up temporary user directory and initialize UserService")
    private void setUp() throws IOException {
        this.userPath = tempDir.resolve("users");
        Files.createDirectories(userPath);
        this.userService = new UserService();
        ReflectionTestUtils.setField(userService, "userPath", userPath.toString() + File.separator);
        user = new User("testUser1", "password123");
        userService.saveUser(user);
    }

    @Test
    @DisplayName("Test UserService constructors and default paths")
    @Tag("constructor")
    public void testConstructors() throws Exception {
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

    @Test
    @DisplayName("Test confirmation of new valid user")
    @Tag("validation")
    public void testConfirmNewValidUser() {
        assertTrue(userService.confirmNewValidUser("testUser", "password123", "password123"));
        assertFalse(userService.confirmNewValidUser("", "password123", "password123"));
        assertFalse(userService.confirmNewValidUser("testUser", "", "password123"));
        assertFalse(userService.confirmNewValidUser("testUser", "password123", ""));
        assertFalse(userService.confirmNewValidUser("us", "password123", "password123"));
        assertFalse(userService.confirmNewValidUser("testUser", "password123", "password124"));
        assertFalse(userService.confirmNewValidUser("testUser", "passo", "passo"));
    }

    @Test
    @DisplayName("Test user validation error messages")
    @Tag("validation")
    public void testGetUserValidationErrorMessage() throws IOException {
        String username = "newUser";
        String password = "password123";
        String confirmPassword = "password123";

        assertFalse(userService.userExists(username));
        assertEquals("", userService.getUserValidationErrorMessage(username, password, confirmPassword));

        userService.saveUser(new User(username, password));
        assertEquals("User already exists", userService.getUserValidationErrorMessage(username, password, confirmPassword));
        assertEquals("Fields cannot be empty", userService.getUserValidationErrorMessage("", password, confirmPassword));
        assertEquals("Fields cannot be empty", userService.getUserValidationErrorMessage(username, "", confirmPassword));
        assertEquals("Fields cannot be empty", userService.getUserValidationErrorMessage(username, password, ""));
        assertEquals("Username must be at least 3 characters long", userService.getUserValidationErrorMessage("us", password, confirmPassword));
        assertEquals("Passwords do not match", userService.getUserValidationErrorMessage("new-test-user", password, "differentPassword"));
        assertEquals("Password must be at least 6 characters long", userService.getUserValidationErrorMessage("new-test-user", "passo", "passo"));
    }

    @Test
    @DisplayName("Test loading an existing user")
    @Tag("persistence")
    public void testLoadUser() throws IOException {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        Optional<User> loadedUserOptional = userService.loadUser(user.getUsername(), "password123");
        assertTrue(loadedUserOptional.isPresent());
        User loadedUser = loadedUserOptional.get();

        assertEquals(user.getUsername(), loadedUser.getUsername());
        assertTrue(passwordEncoder.matches("password123", loadedUser.getHashedPassword()));

        Optional<User> incorrectPasswordUser = userService.loadUser(user.getUsername(), "wrongPassword");
        assertFalse(incorrectPasswordUser.isPresent());

        Optional<User> nonExistingUser = userService.loadUser("nonExistingUser", "password123");
        assertFalse(nonExistingUser.isPresent());
    }

    @Test
    @DisplayName("Test saving a new user")
    @Tag("persistence")
    public void testSaveUser() throws IOException {
        Path userFilePath = userPath.resolve(user.getUsername() + ".json");
        assertTrue(Files.exists(userFilePath));
        assertThrows(IllegalArgumentException.class, () -> userService.saveUser(user));
    }

    @Test
    @DisplayName("Test checking if a user exists")
    @Tag("validation")
    public void testUserExists() throws IOException {
        assertTrue(userService.userExists(user.getUsername()));
        assertFalse(userService.userExists("nonExistingUser"));
    }

    @Test
    @DisplayName("Test assign group to user and update user")
    @Tag("update")
    public void testAssignGroupToUser() throws IOException {
        String groupName = "testGroup";
        userService.assignGroupToUser(user.getUsername(), groupName);

        Optional<User> updatedUserOpt = userService.getUser(user.getUsername());
        assertTrue(updatedUserOpt.isPresent());
        User updatedUser = updatedUserOpt.get();
        assertTrue(updatedUser.getUserGroups().contains(groupName));

        String nonExistentUser = "nonExistentUser";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.assignGroupToUser(nonExistentUser, groupName));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("Test removing a user")
    @Tag("remove")
    public void testRemoveUser() throws IOException {
        assertTrue(userService.userExists(user.getUsername()));
        userService.removeUser(user.getUsername());
        assertFalse(userService.userExists(user.getUsername()));

        Path userFilePath = userPath.resolve(user.getUsername() + ".json");
        assertFalse(Files.exists(userFilePath));

        assertDoesNotThrow(() -> userService.removeUser("nonExistentUser"));
    }

    @Test
    @DisplayName("Test multiple group assignments and edge cases")
    @Tag("groups")
    public void testMultipleGroupAssignments() throws IOException {
        String[] groupNames = {"group1", "group2", "group3"};

        for (String groupName : groupNames) {
            userService.assignGroupToUser(user.getUsername(), groupName);
        }

        Optional<User> updatedUserOpt = userService.getUser(user.getUsername());
        assertTrue(updatedUserOpt.isPresent());
        User updatedUser = updatedUserOpt.get();

        for (String groupName : groupNames) {
            assertTrue(updatedUser.getUserGroups().contains(groupName));
        }

        userService.assignGroupToUser(user.getUsername(), groupNames[0]);
        updatedUserOpt = userService.getUser(user.getUsername());
        assertTrue(updatedUserOpt.isPresent());
        assertEquals(3, updatedUserOpt.get().getUserGroups().size());

        assertThrows(IllegalArgumentException.class, () -> userService.assignGroupToUser(user.getUsername(), null));
    }

    @Test
    @DisplayName("Test constructor with custom path")
    @Tag("constructor")
    public void testCustomPathConstructor() throws Exception {
        Path customPath = tempDir.resolve("customUsers");
        Files.createDirectories(customPath);

        UserService customUserService = new UserService(customPath);

        User testUser = new User("customUser", "password123");
        customUserService.saveUser(testUser);

        Path userFilePath = customPath.resolve(testUser.getUsername() + ".json");
        assertTrue(Files.exists(userFilePath));

        assertTrue(customUserService.userExists(testUser.getUsername()));
        Optional<User> loadedUser = customUserService.loadUser(testUser.getUsername(), "password123");
        assertTrue(loadedUser.isPresent());
        assertEquals(testUser.getUsername(), loadedUser.get().getUsername());
    }
}
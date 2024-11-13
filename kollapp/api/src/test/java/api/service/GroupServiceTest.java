package api.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.databind.ObjectMapper;

import core.User;
import core.UserGroup;

@Tag("unit")
public class GroupServiceTest {

    private GroupService groupService;
    private UserService userService;
    private User user;
    private User user2;

    @TempDir
    Path tempDir;

    private Path groupPath;
    private Path groupToDoListPath;
    private Path userPath;

    @BeforeEach
    public void setUp() throws IOException {
        this.groupPath = tempDir.resolve("groups");
        this.groupToDoListPath = tempDir.resolve("grouptodolists");
        this.userPath = tempDir.resolve("users");

        // Set up directories in the temporary directory
        createDirectory(groupPath);
        createDirectory(groupToDoListPath);
        createDirectory(userPath);

        // Initialize UserService with userPath
        this.userService = new UserService(userPath);

        // Initialize GroupService with paths and userService
        this.groupService = new GroupService(groupPath, groupToDoListPath, userService);

        user = new User("testUser1", "password123");
        user2 = new User("testUser2", "password456");
        userService.saveUser(user);
        userService.saveUser(user2);
    }

    @AfterEach
    public void tearDown() throws IOException {
        deleteDirectory(tempDir);
    }

    @Test
    @DisplayName("Test creating a new group")
    @Tag("group")
    void testCreateGroup() throws IOException {
        String groupName = "testGroup";

        groupService.createGroup(user.getUsername(), groupName);

        Path groupFilePath = groupPath.resolve(groupName + ".json");
        assertTrue(Files.exists(groupFilePath));

        Path groupToDoListFilePath = groupToDoListPath.resolve(groupName + ".json");
        assertTrue(Files.exists(groupToDoListFilePath));

        ObjectMapper mapper = new ObjectMapper();
        UserGroup group = mapper.readValue(groupFilePath.toFile(), UserGroup.class);
        assertNotNull(group);
        assertEquals(groupName, group.getGroupName());
        assertTrue(group.getUsers().contains(user.getUsername()));
    }

    @Test
    @DisplayName("Test retrieving an existing group")
    @Tag("group")
    void testGetGroup() throws IOException {
        String groupName = "testGroup";

        groupService.createGroup(user.getUsername(), groupName);
        Optional<UserGroup> groupOptional = groupService.getGroup(groupName);
        assertTrue(groupOptional.isPresent());

        UserGroup group = groupOptional.get();
        assertNotNull(group);
        assertEquals(groupName, group.getGroupName());
        assertTrue(group.getUsers().contains(user.getUsername()));
    }

    @Test
    @DisplayName("Test getGroup throws IllegalArgumentException when file is corrupted")
    @Tag("group")
    void testGetGroupThrowsExceptionWithCorruptFile() throws IOException {
        String groupName = "corruptGroup";
        Path groupFilePath = groupPath.resolve(groupName + ".json");
        
        // Create a corrupted JSON file
        Files.write(groupFilePath, "{ this is not valid JSON }".getBytes());
        
        // Verify that the expected exception is thrown
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> groupService.getGroup(groupName)
        );
        
        // Verify the exception message starts with "Error reading the group file"
        assertTrue(exception.getMessage().startsWith("Error reading the group file"));
    }

    @Test
    @DisplayName("Test assigning a user to a group")
    @Tag("group")
    void testAssignUserToGroup() throws IOException {
        String groupName = "testGroup";

        groupService.createGroup(user.getUsername(), groupName);
        groupService.assignUserToGroup(user2.getUsername(), groupName);

        ObjectMapper mapper = new ObjectMapper();
        Path groupFilePath = groupPath.resolve(groupName + ".json");
        UserGroup group = mapper.readValue(groupFilePath.toFile(), UserGroup.class);

        assertNotNull(group);
        assertTrue(group.getUsers().contains(user2.getUsername()));
    }

    private void createDirectory(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    private void deleteDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            try (Stream<Path> groupStream = Files.walk(path)) {
                groupStream.sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(file -> {
                            if (!file.delete()) {
                                System.out.println("Failed to delete: " + file.getAbsolutePath());
                            }
                        });
            }
        }
    }

    @Test
    @DisplayName("Test getting a non-existent group returns empty Optional")
    @Tag("group")
    void testGetNonExistentGroup() {
        String nonExistentGroupName = "nonExistentGroup";
        
        Optional<UserGroup> groupOptional = groupService.getGroup(nonExistentGroupName);
        
        assertTrue(groupOptional.isEmpty());
    }

    @Test
    @DisplayName("Test groupExists returns true for existing group")
    @Tag("group")
    void testGroupExistsForExistingGroup() {
        String groupName = "testGroup";
        
        // Create a group first
        groupService.createGroup(user.getUsername(), groupName);
        
        // Verify the group exists
        assertTrue(groupService.groupExists(groupName));
    }

    @Test
    @DisplayName("Test getGroup returns correct group content")
    @Tag("group")
    void testGetGroupContent() {
        String groupName = "testGroup";
        String username = user.getUsername();
        
        // Create a group
        groupService.createGroup(username, groupName);
        
        // Get the group and verify its contents
        Optional<UserGroup> groupOptional = groupService.getGroup(groupName);
        
        assertTrue(groupOptional.isPresent());
        UserGroup group = groupOptional.get();
        assertEquals(groupName, group.getGroupName());
        assertTrue(group.getUsers().contains(username));
        assertEquals(1, group.getUsers().size());
    }

    @Test
    @DisplayName("Test default constructor initialization")
    @Tag("constructor")
    void testDefaultConstructor() throws IOException {
        // Create an instance using the default constructor
        GroupService service = new GroupService();
        
        // Create a user using the default paths that the default constructor uses
        Path defaultUserPath = Paths.get("..", "persistence", "src", "main", "java", "persistence", "users")
                .toAbsolutePath()
                .normalize();
                
        Path defaultGroupPath = Paths.get("..", "persistence", "src", "main", "java", "persistence", "groups")
                .toAbsolutePath()
                .normalize();
                
        Path defaultGroupToDoListPath = Paths.get("..", "persistence", "src", "main", "java", "persistence", "grouptodolists")
                .toAbsolutePath()
                .normalize();
        
        // Create the necessary directories if they don't exist
        Files.createDirectories(defaultUserPath);
        Files.createDirectories(defaultGroupPath);
        Files.createDirectories(defaultGroupToDoListPath);
        
        // Create and save a user in the default location
        User defaultUser = new User("defaultTestUser", "password123");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(defaultUserPath.resolve(defaultUser.getUsername() + ".json").toFile(), defaultUser);
        
        try {
            // Use the service with the newly created user
            String groupName = "testGroup";
            service.createGroup(defaultUser.getUsername(), groupName);
            
            // Verify the group was created in the default location
            Path expectedGroupPath = defaultGroupPath.resolve(groupName + ".json");
            assertTrue(expectedGroupPath.toFile().exists());
        } finally {
            // Clean up - delete all created files
            Files.deleteIfExists(defaultUserPath.resolve(defaultUser.getUsername() + ".json"));
            Files.deleteIfExists(defaultGroupPath.resolve("testGroup.json"));
            Files.deleteIfExists(defaultGroupToDoListPath.resolve("testGroup.json"));
        }
    }

    @Test
    @DisplayName("Test validateGroupAssignment with non-existent user")
    @Tag("validation")
    void testValidateGroupAssignmentNonExistentUser() {
        String groupName = "testGroup";
        
        // Create a group first
        groupService.createGroup(user.getUsername(), groupName);
        
        // Test with non-existent user
        String result = groupService.validateGroupAssignment("nonExistentUser", groupName);
        assertEquals("User does not exist", result);
    }

    @Test
    @DisplayName("Test validateGroupAssignment with non-existent group")
    @Tag("validation")
    void testValidateGroupAssignmentNonExistentGroup() {
        String result = groupService.validateGroupAssignment(user.getUsername(), "nonExistentGroup");
        assertEquals("Group does not exist", result);
    }

    @Test
    @DisplayName("Test validateGroupAssignment with user already in group")
    @Tag("validation")
    void testValidateGroupAssignmentUserAlreadyInGroup() {
        String groupName = "testGroup";
        
        // Create a group with user1
        groupService.createGroup(user.getUsername(), groupName);
        
        // Try to validate assignment for the same user
        String result = groupService.validateGroupAssignment(user.getUsername(), groupName);
        assertEquals("User is already a member of this group", result);
    }

    @Test
    @DisplayName("Test validateGroupAssignment with valid new assignment")
    @Tag("validation")
    void testValidateGroupAssignmentValidAssignment() {
        String groupName = "testGroup";
        
        // Create a group with user1
        groupService.createGroup(user.getUsername(), groupName);
        
        // Validate assignment for user2 (who isn't in the group yet)
        String result = groupService.validateGroupAssignment(user2.getUsername(), groupName);
        assertEquals("", result);
    }
}
package api;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.databind.ObjectMapper;

import api.service.GroupService;
import api.service.UserService;
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
}
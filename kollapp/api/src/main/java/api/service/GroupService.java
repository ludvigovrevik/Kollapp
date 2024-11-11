package api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import core.ToDoList;
import core.User;
import core.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Service class for managing user groups in the application.
 * Provides methods for creating groups, assigning users to groups, and managing group to-do lists.
 * Group and to-do list data is stored as JSON files.
 * 
 * <p>Uses Jackson for JSON handling and UserService for user validation.</p>
 * 
 * @see UserService
 * @see UserGroup
 * @see ToDoList
 */
@Service
public class GroupService {

    private final UserService userService;
    private final ObjectMapper mapper;
    private final Path groupPath;
    private final Path groupToDoListPath;

    /**
     * Constructs a GroupService with default paths for group and group to-do list storage.
     */
    @Autowired
    public GroupService() {
        this(
            Paths.get("..", "persistence", "src", "main", "java", "persistence", "groups").toAbsolutePath().normalize(),
            Paths.get("..", "persistence", "src", "main", "java", "persistence", "grouptodolists").toAbsolutePath().normalize(),
            new UserService()
        );
    }

    /**
     * Constructs a GroupService with specified paths for group and group to-do list storage.
     * 
     * @param groupPath the path for storing group data
     * @param groupToDoListPath the path for storing group to-do lists
     * @param userService the UserService for validating user existence
     */
    public GroupService(Path groupPath, Path groupToDoListPath, UserService userService) {
        this.groupPath = groupPath;
        this.groupToDoListPath = groupToDoListPath;
        this.userService = new UserService(Paths.get(userService.getUserPath()));
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    /**
     * Retrieves a group based on the group name.
     * 
     * @param groupName the name of the group to retrieve
     * @return an Optional containing the UserGroup if found, or an empty Optional otherwise
     * @throws IllegalArgumentException if reading the group file fails
     */
    public Optional<UserGroup> getGroup(String groupName) {
        Path groupFilePath = groupPath.resolve(groupName + ".json");
        File file = groupFilePath.toFile();
        if (!file.exists()) {
            return Optional.empty();
        }
        try {
            return Optional.of(mapper.readValue(file, UserGroup.class));
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading the group file: " + e.getMessage(), e);
        }
    }

    /**
     * Creates a new group with the specified group name and assigns the specified user as the initial member.
     * Creates a new to-do list for the group.
     * 
     * @param username the username of the user creating the group
     * @param groupName the name of the group to create
     * @throws IllegalArgumentException if the user does not exist
     * @throws RuntimeException if group creation fails
     */
    public void createGroup(String username, String groupName) {
        User user = userService.getUser(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        UserGroup userGroup = new UserGroup(groupName);
        userGroup.addUser(user.getUsername());

        ToDoList toDoList = new ToDoList();
        Path groupFilePath = groupPath.resolve(groupName + ".json");
        Path groupToDoListFilePath = groupToDoListPath.resolve(groupName + ".json");

        try {
            Files.createDirectories(groupPath);
            Files.createDirectories(groupToDoListPath);

            mapper.writeValue(groupFilePath.toFile(), userGroup);
            mapper.writeValue(groupToDoListFilePath.toFile(), toDoList);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create group: " + groupName, e);
        }
    }

    /**
     * Assigns a user to an existing group.
     * 
     * @param username the username of the user to add to the group
     * @param groupName the name of the group to add the user to
     * @throws IllegalArgumentException if the user or group does not exist
     * @throws RuntimeException if updating the group file fails
     */
    public void assignUserToGroup(String username, String groupName) {
        User user = userService.getUser(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        UserGroup userGroup = getGroup(groupName)
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + groupName));

        userGroup.addUser(user.getUsername());

        Path groupFilePath = groupPath.resolve(groupName + ".json");

        try {
            mapper.writeValue(groupFilePath.toFile(), userGroup);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update group file for group: " + groupName, e);
        }
    }

    /**
     * Checks if a group exists based on its name.
     * 
     * @param groupName the name of the group to check
     * @return true if the group exists, false otherwise
     */
    public boolean groupExists(String groupName) {
        Path groupFilePath = groupPath.resolve(groupName + ".json");
        return groupFilePath.toFile().exists();
    }

    public String validateGroupAssignment(String username, String groupName) {
    // Validate if user exists
    if (!userService.userExists(username)) {
        return "User does not exist";
    }

    // Validate if group exists
    if (!groupExists(groupName)) {
        return "Group does not exist";
    }

    // Get the group
    UserGroup userGroup = getGroup(groupName)
            .orElseThrow(() -> new IllegalArgumentException("Group not found: " + groupName));

    // Check if user is already in the group
    if (userGroup.getUsers().contains(username)) {
        return "User is already a member of this group";
    }

    return null; // null indicates no validation errors
    }   
}
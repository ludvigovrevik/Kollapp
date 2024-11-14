package api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import core.ToDoList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Service class for managing to-do lists for individual users and groups.
 * This service allows for creating, loading, and updating user and group to-do lists,
 * storing each to-do list as a JSON file.
 * 
 * <p>Uses Jackson for JSON handling and UserService for user validation.</p>
 * 
 * @see UserService
 * @see ToDoList
 */
@Service
public class ToDoListService {

    private final UserService userService;
    private final ObjectMapper mapper;
    private final Path toDoListPath;
    private final Path groupToDoListPath;

    /**
     * Constructs a ToDoListService with default paths for user and group to-do lists.
     */
    @Autowired
    public ToDoListService() {
        this(
            Paths.get("..", "persistence", "src", "main", "java", "persistence", "todolists").toAbsolutePath().normalize(),
            Paths.get("..", "persistence", "src", "main", "java", "persistence", "grouptodolists").toAbsolutePath().normalize(),
            new UserService()
        );
    }

    /**
     * Constructs a ToDoListService with specified paths for user and group to-do lists.
     * 
     * @param toDoListPath the path for storing user to-do lists
     * @param groupToDoListPath the path for storing group to-do lists
     * @param userService the UserService for validating user existence
     */
    public ToDoListService(Path toDoListPath, Path groupToDoListPath, UserService userService) {
        if (toDoListPath == null || groupToDoListPath == null || userService == null) {
            throw new NullPointerException("Arguments cannot be null");
        }
        this.toDoListPath = toDoListPath;
        this.groupToDoListPath = groupToDoListPath;
        this.userService = new UserService(Paths.get(userService.getUserPath()));
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    /**
     * Assigns a new, empty to-do list to a user if the user exists.
     * 
     * @param username the username to assign the to-do list to
     * @throws IllegalArgumentException if the user does not exist or assignment fails
     */
    public void assignToDoList(String username) {
        if (userService.userExists(username)) {
            ToDoList toDoList = new ToDoList();
            Path filePath = toDoListPath.resolve(username + ".json");
            try {
                Files.createDirectories(toDoListPath);
                mapper.writeValue(filePath.toFile(), toDoList);
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to assign to-do list to user: " + username, e);
            }
        } else {
            throw new IllegalArgumentException("User not found: " + username);
        }
    }

    /**
     * Loads an existing to-do list for a user if the user exists and has a to-do list file.
     * 
     * @param username the username whose to-do list is to be loaded
     * @return the user's ToDoList
     * @throws IllegalArgumentException if the user or to-do list file does not exist, or loading fails
     */
    public ToDoList loadToDoList(String username) {
        if (userService.userExists(username)) {
            Path filePath = toDoListPath.resolve(username + ".json");
            if (!Files.exists(filePath)) {
                throw new IllegalArgumentException("To-do list file does not exist for user: " + username);
            }
            try {
                return mapper.readValue(filePath.toFile(), ToDoList.class);
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to load to-do list for user: " + username, e);
            }
        } else {
            throw new IllegalArgumentException("User not found: " + username);
        }
    }

    /**
     * Updates the to-do list for a user if the user exists.
     * 
     * @param username the username whose to-do list is to be updated
     * @param toDoList the ToDoList object to save
     * @throws IllegalArgumentException if the user does not exist or updating fails
     */
    public void updateToDoList(String username, ToDoList toDoList) {
        if (userService.userExists(username)) {
            Path filePath = toDoListPath.resolve(username + ".json");
            try {
                Files.createDirectories(toDoListPath);
                mapper.writeValue(filePath.toFile(), toDoList);
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to update to-do list for user: " + username, e);
            }
        } else {
            throw new IllegalArgumentException("User not found: " + username);
        }
    }

    /**
     * Loads an existing to-do list for a group. Returns an empty to-do list if none exists.
     * 
     * @param groupName the group name whose to-do list is to be loaded
     * @return the group's ToDoList
     * @throws IllegalArgumentException if loading fails
     */
    public ToDoList loadGroupToDoList(String groupName) {
        Path filePath = groupToDoListPath.resolve(groupName + ".json");
        if (!Files.exists(filePath)) {
            return new ToDoList();
        }
        try {
            return mapper.readValue(filePath.toFile(), ToDoList.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to load group to-do list for group: " + groupName, e);
        }
    }

    /**
     * Updates the to-do list for a group.
     * 
     * @param groupName the group name whose to-do list is to be updated
     * @param toDoList the ToDoList object to save
     * @throws IllegalArgumentException if updating fails
     */
    public void updateGroupToDoList(String groupName, ToDoList toDoList) {
        Path filePath = groupToDoListPath.resolve(groupName + ".json");
        try {
            Files.createDirectories(groupToDoListPath);
            mapper.writeValue(filePath.toFile(), toDoList);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to update group to-do list for group: " + groupName, e);
        }
    }
}
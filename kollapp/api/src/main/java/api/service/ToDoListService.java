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

@Service
public class ToDoListService {

    private final UserService userService;
    private final ObjectMapper mapper;
    private final Path toDoListPath;
    private final Path groupToDoListPath;

    @Autowired
    public ToDoListService() {
        this(
            Paths.get("..", "persistence", "src", "main", "java", "persistence", "todolists").toAbsolutePath().normalize(),
            Paths.get("..", "persistence", "src", "main", "java", "persistence", "grouptodolists").toAbsolutePath().normalize(),
            new UserService()
        );
    }

    public ToDoListService(Path toDoListPath, Path groupToDoListPath, UserService userService) {
        this.toDoListPath = toDoListPath;
        this.groupToDoListPath = groupToDoListPath;
        this.userService = userService;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

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
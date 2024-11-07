package api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import core.ToDoList;
import core.User;
import core.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class GroupService {

    private final UserService userService;
    private final ObjectMapper mapper;
    private final Path groupPath;
    private final Path groupToDoListPath;

    @Autowired
    public GroupService() {
        this(
            Paths.get("..", "persistence", "src", "main", "java", "persistence", "groups").toAbsolutePath().normalize(),
            Paths.get("..", "persistence", "src", "main", "java", "persistence", "grouptodolists").toAbsolutePath().normalize(),
            new UserService()
        );
    }

    public GroupService(Path groupPath, Path groupToDoListPath, UserService userService) {
        this.groupPath = groupPath;
        this.groupToDoListPath = groupToDoListPath;
        this.userService = userService;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

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

    public boolean groupExists(String groupName) {
        Path groupFilePath = groupPath.resolve(groupName + ".json");
        return groupFilePath.toFile().exists();
    }
}
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class GroupService {

    private final UserService userService;
    private final ObjectMapper mapper;
    private final String groupPath;
    private final String groupToDoListPath;

    @Autowired
    public GroupService() {
        this.groupPath = Paths.get("..", "persistence", "src", "main", "java",
                "persistence", "groups").toAbsolutePath()
                .normalize().toString() + File.separator;
        this.groupToDoListPath = Paths.get("..", "persistence", "src", "main", "java",
                "persistence", "grouptodolists").toAbsolutePath()
                .normalize().toString() + File.separator;
        this.userService = new UserService();
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    public GroupService(Path groupPath, Path groupToDoListPath) {
        this.groupPath = groupPath.toAbsolutePath()
                .normalize().toString() + File.separator;
        this.groupToDoListPath = groupToDoListPath.toAbsolutePath()
                .normalize().toString() + File.separator;
        this.userService = new UserService();
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    public Optional<UserGroup> getGroup(String groupName) {
        File file = new File(groupPath + groupName + ".json");
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
        File groupFile = new File(groupPath + groupName + ".json");
        File groupToDoListFile = new File(groupToDoListPath + groupName + ".json");

        try {
            mapper.writeValue(groupFile, userGroup);
            mapper.writeValue(groupToDoListFile, toDoList);
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

        File groupFile = new File(groupPath + groupName + ".json");

        try {
            mapper.writeValue(groupFile, userGroup);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update group file for group: " + groupName, e);
        }
    }

    public boolean groupExists(String groupName) {
        File file = new File(groupPath + groupName + ".json");
        return file.exists();
    }
}
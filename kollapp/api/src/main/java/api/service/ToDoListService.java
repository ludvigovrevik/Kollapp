package api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import core.ToDoList;
import core.User;
import core.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import persistence.UserHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class ToDoListService {

    private final UserHandler userHandler;
    private final ObjectMapper mapper;
    private final String toDoListPath;
    private final String groupToDoListPath;

    @Autowired
    public ToDoListService() {
        this.userHandler = new UserHandler(
                Paths.get("..", "persistence", "src", "main", "java", "persistence", "users").toAbsolutePath().normalize().toString() + File.separator);
        
        this.toDoListPath = Paths.get("..", "persistence", "src", "main", "java", "persistence", "todolists").toAbsolutePath().normalize().toString() + File.separator;
        this.groupToDoListPath = Paths.get("..", "persistence", "src", "main", "java", "persistence", "grouptodolists").toAbsolutePath().normalize().toString() + File.separator;
        
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    public ToDoListService(Path todolistPath, Path groupToDoListPath) {
        this.userHandler = new UserHandler(
                todolistPath.toAbsolutePath().normalize().toString() + File.separator);
        
        this.toDoListPath = groupToDoListPath.toAbsolutePath().normalize().toString() + File.separator;
        this.groupToDoListPath = Paths.get("..", "persistence", "src", "main", "java", "persistence", "grouptodolists").toAbsolutePath().normalize().toString() + File.separator;
        
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    public void assignToDoList(String username) {
        Optional<User> userOpt = userHandler.getUser(username);
        if (userOpt.isPresent()) {
            ToDoList toDoList = new ToDoList();
            File file = new File(toDoListPath + username + ".json");
            try {
                mapper.writeValue(file, toDoList);
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to assign to-do list to user: " + username, e);
            }
        } else {
            throw new IllegalArgumentException("User not found: " + username);
        }
    }

    public ToDoList loadToDoList(String username) {
        Optional<User> userOpt = userHandler.getUser(username);
        if (userOpt.isPresent()) {
            File file = new File(toDoListPath + username + ".json");
            if (!file.exists()) {
                throw new IllegalArgumentException("To-do list file does not exist for user: " + username);
            }
            try {
                return mapper.readValue(file, ToDoList.class);
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to load to-do list for user: " + username, e);
            }
        } else {
            throw new IllegalArgumentException("User not found: " + username);
        }
    }

    public void updateToDoList(String username, ToDoList toDoList) {
        Optional<User> userOpt = userHandler.getUser(username);
        if (userOpt.isPresent()) {
            File file = new File(toDoListPath + username + ".json");
            try {
                mapper.writeValue(file, toDoList);
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to update to-do list for user: " + username, e);
            }
        } else {
            throw new IllegalArgumentException("User not found: " + username);
        }
    }

    public ToDoList loadGroupToDoList(String groupName) {
        File file = new File(groupToDoListPath + groupName + ".json");
        if (!file.exists()) {
            return new ToDoList();
        }
        try {
            return mapper.readValue(file, ToDoList.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to load group to-do list for group: " + groupName, e);
        }
    }

    public void updateGroupToDoList(String groupName, ToDoList toDoList) {
        File file = new File(groupToDoListPath + groupName + ".json");
        try {
            mapper.writeValue(file, toDoList);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to update group to-do list for group: " + groupName, e);
        }
    }
}
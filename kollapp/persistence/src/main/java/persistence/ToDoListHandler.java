package persistence;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import core.ToDoList;
import core.User;
import core.UserGroup;

public class ToDoListHandler {
    private final String toDoListPath;
    private final String groupToDoListPath;
    private ObjectMapper mapper;

    // Default constructor uses the original path
    public ToDoListHandler() {
        this.toDoListPath = Paths.get("..", "persistence", "src", "main", "java", "persistence", "todolists").toString() + File.separator;
        this.groupToDoListPath = Paths.get("..", "persistence", "src", "main", "java", "persistence", "grouptodolists").toString() + File.separator;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule()); // Register the module
    }

    // New constructor that accepts a custom path
    public ToDoListHandler(String todolistPath, String grouptodolistPath) {
        this.toDoListPath = todolistPath;
        this.groupToDoListPath = grouptodolistPath;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule()); // Register the module
    }

    public void assignToDoList(User user) {
        ToDoList toDoList = new ToDoList();
        File file = new File(toDoListPath + user.getUsername() + ".json");
        try {
            mapper.writeValue(file, toDoList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public ToDoList loadToDoList(User user) {
        File file = new File(toDoListPath + user.getUsername() + ".json");
        if (!file.exists()) {
            throw new IllegalArgumentException("To-do list file does not exist for user: " + user.getUsername());
        }
        try {
            return mapper.readValue(file, ToDoList.class);
        } catch (IOException e) {
            System.out.println("Failed to load to-do list for user: " + user.getUsername());
            throw new IllegalArgumentException(e);
        }
    }
    
    public void updateToDoList(User user, ToDoList toDoList) {
        File file = new File(toDoListPath + user.getUsername() + ".json");
        try {
            mapper.writeValue(file, toDoList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ToDoList loadGroupToDoList(UserGroup userGroup) {
        String groupName = userGroup.getGroupName();
        File file = new File(groupToDoListPath + groupName + ".json");
        if (!file.exists()) {
            System.out.println("File not found for user: " + groupName);
            return new ToDoList(); // Return an empty list instead of null if file doesn't exist
        }
        try {
            return mapper.readValue(file, ToDoList.class);
        } catch (IOException e) {
            System.out.println("Failed to load to-do list for group: " + groupName);
            e.printStackTrace();
        }
        return new ToDoList(); // Return an empty list instead of null if an exception occurs
    }

    public void updateGroupToDoList(UserGroup userGroup, ToDoList toDoList) {
        File file = new File(groupToDoListPath + userGroup.getGroupName() + ".json");
        if (!file.exists()) {
            throw new IllegalArgumentException("To-do list file does not exist for group: " + userGroup.getGroupName());
        }
        try {
            mapper.writeValue(file, toDoList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
